package com.huariservice.huariia.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huariservice.huariia.dto.senado.SenadoMatterDto;
import com.huariservice.huariia.dto.senado.SenadoSenatorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SenadoIntegrationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;
    private SenadoIntegrationService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new SenadoIntegrationService(
                restClient,
                objectMapper,
                "https://legis.senado.leg.br/dadosabertos"
        );
    }

    @Test
    @DisplayName("Should fetch and parse active senators successfully from Senado API")
    void shouldFetchSenatorsSuccessfully() {
        String json = """
                {
                  "ListaParlamentarEmExercicio": {
                    "Parlamentares": {
                      "Parlamentar": [
                        {
                          "IdentificacaoParlamentar": {
                            "CodigoParlamentar": "5980",
                            "NomeParlamentar": "Rodrigo Pacheco",
                            "NomeCompletoParlamentar": "Rodrigo Otavio Soares Pacheco",
                            "SexoParlamentar": "Masculino",
                            "FormaTratamento": "Senador",
                            "UrlFotoParlamentar": "https://www.senado.leg.br/senadores/img/fotos-55/5980.jpg",
                            "SiglaPartidoParlamentar": "PSD",
                            "UfParlamentar": "MG",
                            "EmailParlamentar": "rodrigo.pacheco@senado.leg.br"
                          },
                          "Mandato": {
                            "DescricaoParticipacao": "Titular"
                          }
                        },
                        {
                          "IdentificacaoParlamentar": {
                            "CodigoParlamentar": "5529",
                            "NomeParlamentar": "Mara Gabrilli",
                            "NomeCompletoParlamentar": "Mara Cristina Gabrilli",
                            "SexoParlamentar": "Feminino",
                            "FormaTratamento": "Senadora",
                            "UrlFotoParlamentar": "https://www.senado.leg.br/senadores/img/fotos-55/5529.jpg",
                            "SiglaPartidoParlamentar": "PSD",
                            "UfParlamentar": "SP",
                            "EmailParlamentar": "sen.maragabrilli@senado.leg.br"
                          },
                          "Mandato": {
                            "DescricaoParticipacao": "Titular"
                          }
                        }
                      ]
                    }
                  }
                }
                """;

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        List<SenadoSenatorDto> senators = service.fetchCurrentSenators("SP", "PSD");

        assertNotNull(senators);
        assertEquals(1, senators.size());
        assertEquals("Mara Gabrilli", senators.get(0).nomeParlamentar());
        assertEquals("SP", senators.get(0).siglaUf());
    }

    @Test
    @DisplayName("Should return empty list on network failure when fetching senators")
    void shouldReturnEmptyListWhenSenatorsCallFails() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Senado API down"));

        List<SenadoSenatorDto> senators = service.fetchCurrentSenators(null, null);

        assertNotNull(senators);
        assertTrue(senators.isEmpty());
    }

    @Test
    @DisplayName("Should fetch and parse legislative matters from Senado API")
    void shouldFetchMattersSuccessfully() {
        String json = """
                {
                  "PesquisaBasicaMateria": {
                    "Materias": {
                      "Materia": [
                        {
                          "IdentificacaoMateria": {
                            "CodigoMateria": "161234",
                            "SiglaSubtipoMateria": "PL",
                            "NumeroMateria": "456",
                            "AnoMateria": "2026"
                          },
                          "DadosBasicosMateria": {
                            "EmentaMateria": "Institui diretrizes para a transparência de dados públicos.",
                            "UrlDetalheMateria": "https://legis.senado.leg.br/materia/161234"
                          },
                          "AutoresPrincipal": {
                            "AutorPrincipal": {
                              "NomeAutor": "Senado Federal"
                            }
                          }
                        }
                      ]
                    }
                  }
                }
                """;

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(json);

        List<SenadoMatterDto> matters = service.fetchMatters("PL", 2026);

        assertNotNull(matters);
        assertEquals(1, matters.size());
        assertEquals("PL", matters.get(0).sigla());
        assertEquals(456, matters.get(0).numero());
    }

    @Test
    @DisplayName("Should return empty list gracefully when matters query fails")
    void shouldReturnEmptyListWhenMattersQueryFails() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenThrow(new ResourceAccessException("Timeout"));

        List<SenadoMatterDto> matters = service.fetchMatters("PL", 2026);

        assertNotNull(matters);
        assertTrue(matters.isEmpty());
    }
}
