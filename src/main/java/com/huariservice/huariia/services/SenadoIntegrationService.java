package com.huariservice.huariia.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huariservice.huariia.dto.senado.SenadoMatterDto;
import com.huariservice.huariia.dto.senado.SenadoSenatorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service orchestrating integration with the Senado Federal Open Data API
 * for active senators and legislative matters with resilient fallbacks.
 */
@Slf4j
@Service
public class SenadoIntegrationService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    @org.springframework.beans.factory.annotation.Autowired
    public SenadoIntegrationService(
            @Qualifier("senadoRestClient") RestClient restClient,
            @Value("${app.senado.base-url:https://legis.senado.leg.br/dadosabertos}") String baseUrl) {
        this(restClient, new ObjectMapper(), baseUrl);
    }

    public SenadoIntegrationService(
            RestClient restClient,
            ObjectMapper objectMapper,
            String baseUrl) {
        this.restClient = restClient;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.baseUrl = baseUrl;
    }

    /**
     * Lists current active federal senators, optionally filtered by state (UF) and party.
     */
    public List<SenadoSenatorDto> fetchCurrentSenators(String state, String party) {
        try {
            String rawJson = restClient.get()
                    .uri("/senador/lista/atual.json")
                    .retrieve()
                    .body(String.class);

            if (rawJson == null || rawJson.isBlank()) {
                return Collections.emptyList();
            }

            List<SenadoSenatorDto> senators = parseSenatorsFromJson(rawJson);

            return senators.stream()
                    .filter(s -> state == null || state.isBlank()
                            || (s.siglaUf() != null && s.siglaUf().equalsIgnoreCase(state.trim())))
                    .filter(s -> party == null || party.isBlank()
                            || (s.siglaPartido() != null && s.siglaPartido().equalsIgnoreCase(party.trim())))
                    .toList();
        } catch (RestClientResponseException ex) {
            log.warn("Senado API returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout or network error connecting to Senado API: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading senators from Senado API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Queries legislative matters from Senado Federal API with defensive fallback.
     */
    public List<SenadoMatterDto> fetchMatters(String sigla, Integer year) {
        try {
            String type = (sigla != null && !sigla.isBlank()) ? sigla.trim().toUpperCase() : "PL";
            String uri = (year != null && year > 0)
                    ? "/materia/pesquisa/lista.json?sigla=" + type + "&ano=" + year
                    : "/materia/pesquisa/lista.json?sigla=" + type;

            String rawJson = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            if (rawJson == null || rawJson.isBlank()) {
                return Collections.emptyList();
            }

            return parseMattersFromJson(rawJson);
        } catch (RestClientResponseException ex) {
            log.warn("Senado API matters returned HTTP status {}: {}", ex.getStatusCode().value(), ex.getMessage());
            return Collections.emptyList();
        } catch (ResourceAccessException ex) {
            log.warn("Timeout connecting to Senado API matters: {}", ex.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.warn("Unexpected error reading matters from Senado API: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<SenadoSenatorDto> parseSenatorsFromJson(String json) {
        List<SenadoSenatorDto> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode parlamentaresNode = root.path("ListaParlamentarEmExercicio")
                    .path("Parlamentares")
                    .path("Parlamentar");

            if (parlamentaresNode.isArray()) {
                for (JsonNode node : parlamentaresNode) {
                    list.add(extractSenator(node));
                }
            } else if (!parlamentaresNode.isMissingNode() && !parlamentaresNode.isNull()) {
                list.add(extractSenator(parlamentaresNode));
            }
        } catch (Exception ex) {
            log.warn("Error parsing Senado senators JSON payload: {}", ex.getMessage());
        }
        return list;
    }

    private SenadoSenatorDto extractSenator(JsonNode node) {
        JsonNode idNode = node.path("IdentificacaoParlamentar");
        Long codigo = idNode.path("CodigoParlamentar").asLong(0L);
        String nomeParlamentar = idNode.path("NomeParlamentar").asText(null);
        String nomeCompleto = idNode.path("NomeCompletoParlamentar").asText(null);
        String sexo = idNode.path("SexoParlamentar").asText(null);
        String formaTratamento = idNode.path("FormaTratamento").asText(null);
        String urlFoto = idNode.path("UrlFotoParlamentar").asText(null);
        String siglaPartido = idNode.path("SiglaPartidoParlamentar").asText(null);
        String siglaUf = idNode.path("UfParlamentar").asText(null);
        String email = idNode.path("EmailParlamentar").asText(null);
        String participacao = node.path("Mandato").path("DescricaoParticipacao").asText(null);

        return new SenadoSenatorDto(
                codigo,
                nomeParlamentar,
                nomeCompleto,
                sexo,
                formaTratamento,
                urlFoto,
                siglaPartido,
                siglaUf,
                email,
                participacao
        );
    }

    private List<SenadoMatterDto> parseMattersFromJson(String json) {
        List<SenadoMatterDto> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode materiasNode = root.path("PesquisaBasicaMateria")
                    .path("Materias")
                    .path("Materia");

            if (materiasNode.isArray()) {
                for (JsonNode node : materiasNode) {
                    list.add(extractMatter(node));
                }
            } else if (!materiasNode.isMissingNode() && !materiasNode.isNull()) {
                list.add(extractMatter(materiasNode));
            }
        } catch (Exception ex) {
            log.warn("Error parsing Senado matters JSON payload: {}", ex.getMessage());
        }
        return list;
    }

    private SenadoMatterDto extractMatter(JsonNode node) {
        JsonNode idNode = node.path("IdentificacaoMateria");
        Long codigo = idNode.path("CodigoMateria").asLong(0L);
        String sigla = idNode.path("SiglaSubtipoMateria").asText("PL");
        Integer numero = idNode.path("NumeroMateria").asInt(0);
        Integer ano = idNode.path("AnoMateria").asInt(0);
        String ementa = node.path("DadosBasicosMateria").path("EmentaMateria").asText(null);
        String autor = node.path("AutoresPrincipal").path("AutorPrincipal").path("NomeAutor").asText(null);
        String url = node.path("DadosBasicosMateria").path("UrlDetalheMateria").asText(null);

        return new SenadoMatterDto(
                codigo,
                sigla,
                numero,
                ano,
                ementa,
                autor,
                url
        );
    }
}
