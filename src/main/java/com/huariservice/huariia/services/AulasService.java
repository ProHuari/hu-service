package com.huariservice.huariia.services;

import com.huariservice.huariia.DTOs.AulaRequest;
import com.huariservice.huariia.DTOs.AulaResponse;
import com.huariservice.huariia.entities.Aula;
import com.huariservice.huariia.entities.Modulo;
import com.huariservice.huariia.exceptions.ConflitoException;
import com.huariservice.huariia.exceptions.RecursoNaoEncontradoException;
import com.huariservice.huariia.repositories.AulaRepository;
import com.huariservice.huariia.repositories.ModuloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AulaService {

    private final AulaRepository aulaRepository;
    private final ModuloRepository moduloRepository;

    public AulaService(AulaRepository aulaRepository, ModuloRepository moduloRepository) {
        this.aulaRepository = aulaRepository;
        this.moduloRepository = moduloRepository;
    }

    public AulaResponse pubAula(AulaRequest request) {
        if (aulaRepository.existsByUrlVideo(request.urlVideo())) {
            throw new ConflitoException("Já existe uma aula com essa URL de vídeo");
        }
        if (aulaRepository.existsByModuloIdAndOrdem(request.moduloId(), request.ordem())) {
            throw new ConflitoException("Já existe uma aula com essa ordem no módulo");
        }

        Aula aula = new Aula();
        preencher(aula, request);

        return AulaResponse.from(aulaRepository.save(aula));
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> mostrarAulas() {
        return aulaRepository.findAll().stream().map(AulaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> mostrarAulasDoModulo(Long moduloId) {
        return aulaRepository.findByModuloIdOrderByOrdemAsc(moduloId).stream()
                .map(AulaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AulaResponse buscarPorId(Long id) {
        return AulaResponse.from(buscarAula(id));
    }

    public AulaResponse mudarAula(Long id, AulaRequest request) {
        Aula aula = buscarAula(id);

        if (!aula.getUrlVideo().equals(request.urlVideo())
                && aulaRepository.existsByUrlVideo(request.urlVideo())) {
            throw new ConflitoException("Já existe uma aula com essa URL de vídeo");
        }

        boolean mudouPosicao = !aula.getModulo().getId().equals(request.moduloId())
                || !aula.getOrdem().equals(request.ordem());
        if (mudouPosicao && aulaRepository.existsByModuloIdAndOrdem(request.moduloId(), request.ordem())) {
            throw new ConflitoException("Já existe uma aula com essa ordem no módulo");
        }

        preencher(aula, request);

        return AulaResponse.from(aulaRepository.save(aula));
    }

    public void deletarId(Long id) {
        aulaRepository.delete(buscarAula(id));
    }

    private void preencher(Aula aula, AulaRequest request) {
        Modulo modulo = moduloRepository.findById(request.moduloId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Esse módulo não foi cadastrado"));

        aula.setTitulo(request.titulo());
        aula.setDescricao(request.descricao());
        aula.setUrlVideo(request.urlVideo());
        aula.setOrdem(request.ordem());
        aula.setDuracaoEmMinutos(request.duracaoEmMinutos());
        aula.setModulo(modulo);
    }

    private Aula buscarAula(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Essa aula não foi publicada"));
    }
}