package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.HistoricoIaResponse;
import com.huariservice.huariia.entities.HistoricoIa;
import com.huariservice.huariia.entities.Usuario;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.HistoricoIaRepository;
import com.huariservice.huariia.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// O histórico é um registro: não há atualização. dataConsulta é preenchida pela entidade.
@Service
@Transactional
@RequiredArgsConstructor
public class HistoricoIaService {

    private final HistoricoIaRepository historicoIaRepository;
    private final UsuarioRepository usuarioRepository;


    // O usuarioId vem do token, nunca do corpo da requisição.

    public HistoricoIaResponse registrarHistorico(
            Long usuarioId,
            String pergunta,
            String resposta) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Esse usuário não foi cadastrado"));

        HistoricoIa historico = new HistoricoIa();
        historico.setPergunta(pergunta);
        historico.setResposta(resposta);
        historico.setUsuario(usuario);

        return HistoricoIaResponse.from(
                historicoIaRepository.save(historico));
    }


    @Transactional(readOnly = true)
    public List<HistoricoIaResponse> mostrarHistoricoIa() {
        return historicoIaRepository.findAll().stream().map(HistoricoIaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoIaResponse> mostrarHistoricoDoUsuario(Long usuarioId) {
        return historicoIaRepository.findByUsuarioIdOrderByDataConsultaDesc(usuarioId).stream()
                .map(HistoricoIaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public HistoricoIaResponse buscarPorId(Long id, Long usuarioId, boolean admin) {
        return HistoricoIaResponse.from(buscarHistorico(id, usuarioId, admin));
    }

    public void deletarId(Long id, Long usuarioId, boolean admin) {
        historicoIaRepository.delete(buscarHistorico(id, usuarioId, admin));
    }

    // Aluno só acessa o próprio histórico. Devolve 404 (e não 403) para não revelar que o id existe.
    private HistoricoIa buscarHistorico(Long id, Long usuarioId, boolean admin) {
        HistoricoIa historico = historicoIaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse histórico não foi registrado"));

        if (!admin && !historico.getUsuario().getId().equals(usuarioId)) {
            throw new RecursoNaoEncontradoException("Esse histórico não foi registrado");
        }
        return historico;
    }
}