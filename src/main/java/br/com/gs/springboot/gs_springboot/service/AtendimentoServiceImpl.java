package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.assembler.AtendimentoModelAssembler;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.exception.InvalidStatusTransitionException;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import br.com.gs.springboot.gs_springboot.model.TipoServico;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.AtendimentoRepository;
import br.com.gs.springboot.gs_springboot.repository.ColaboradorRepository;
import br.com.gs.springboot.gs_springboot.repository.UnidadeSaudeRepository;
import br.com.gs.springboot.gs_springboot.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
public class AtendimentoServiceImpl implements AtendimentoService {
    private final AtendimentoRepository atendimentoRepository;
    private final AtendimentoModelAssembler atendimentoModelAssembler;
    private final PacienteService pacienteService;
    private final UnidadeSaudeRepository unidadeRepository;
    private final UnidadeSaudeService unidadeSaudeService;
    private final ColaboradorRepository colaboradorRepository;

    public AtendimentoServiceImpl(AtendimentoRepository atendimentoRepository, AtendimentoModelAssembler atendimentoModelAssembler, PacienteService pacienteService, UnidadeSaudeRepository unidadeRepository, UnidadeSaudeService unidadeSaudeService, ColaboradorRepository colaboradorRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.atendimentoModelAssembler = atendimentoModelAssembler;
        this.pacienteService = pacienteService;
        this.unidadeRepository = unidadeRepository;
        this.unidadeSaudeService = unidadeSaudeService;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    public Atendimento findById(Long id) {
        return atendimentoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atendimento", id));
    }

    @Override
    public EntityModel<AtendimentoResponse> buscarAtendimento(Long id) {
        Atendimento atendimento = findById(id);
        AtendimentoResponse atendimentoDefinido = mapearAtendimentoComPosicaoDaFila(atendimento);
        return atendimentoModelAssembler.toModel(atendimentoDefinido);
    }

    @Override
    public Page<AtendimentoResponse> listarAtendimentosPorUnidade(Long unidadeId, Integer pagina, Integer tamanho){
        return atendimentoRepository.findByUnidadeSaudeIdAndStatusInOrderByDataHoraChegadaAsc(
                unidadeId,
                List.of(StatusAtendimento.CHAMADO, StatusAtendimento.AGUARDANDO),
                Pageable.ofSize(tamanho).withPage(pagina)).map(this::mapearAtendimentoComPosicaoDaFila);
    }

    @Override
    public AtendimentoResponse iniciarAtendimento(AtendimentoCriacaoRequest request) {
        Atendimento atendimento = new Atendimento();

        Long idUnidade = (request.idUnidade() != null) ? request.idUnidade() : 1L;
        UnidadeSaude unidadeSaude = unidadeSaudeService.findById(idUnidade);
        atendimento.setUnidadeSaude(unidadeSaude);

        atendimento.setServico(request.servico());

        atendimento.setPaciente(pacienteService.buscarPorCpf(request.cpf().replaceAll("\\D", ""))
                        .orElseThrow(() -> new ResourceNotFoundException("Paciente", 0L)));

        long proximoNumero = atendimentoRepository.countByUnidadeSaudeIdAndServico(idUnidade, request.servico()) + 1;
        atendimento.setSenha(String.format("%s-%03d", request.servico().getPrefixo(), proximoNumero));

        Atendimento atendimentoSalvo = atendimentoRepository.save(atendimento);
        chamarProximoDaFila(idUnidade, request.servico());
        atualizarTempoEsperaUnidade(atendimento.getUnidadeSaude());

        return mapearAtendimentoComPosicaoDaFila(atendimentoSalvo);
    }

    @Override
    public AtendimentoResponse encerrarAtendimento(Long id, AtendimentoAtualizacaoRequest request) {
        Atendimento atendimento = findById(id);
        if(atendimento.getStatus() == StatusAtendimento.FINALIZADO){
            throw new InvalidStatusTransitionException("Atendimento já consta encerrado!");
        }

        String emailColaborador = SecurityUtils.obterEmailColaborador();

        colaboradorRepository.findByEmail(emailColaborador)
                        .ifPresent(atendimento::setColaborador);

        atendimento.setStatus(StatusAtendimento.FINALIZADO);
        atendimento.setDataHoraFim(LocalDateTime.now());
        Atendimento atendimentoSalvo = atendimentoRepository.save(atendimento);

        chamarProximoDaFila(atendimentoSalvo.getUnidadeSaude().getId(), atendimentoSalvo.getServico());
        atualizarTempoEsperaUnidade(atendimentoSalvo.getUnidadeSaude());

        return mapearAtendimentoComPosicaoDaFila(atendimentoSalvo);
    }

    private void atualizarTempoEsperaUnidade(UnidadeSaude unidade){
        long filaAberta = atendimentoRepository.countByUnidadeSaudeIdAndStatus(unidade.getId(), StatusAtendimento.AGUARDANDO);

        //Estimativa
        double minutosPaciente = 20.0;

        unidade.setTempoEspera(filaAberta * minutosPaciente);
        unidadeRepository.save(unidade);
    }

    private void chamarProximoDaFila(Long idUnidade, TipoServico servico){
        boolean alguemFoiChamado = atendimentoRepository.existsByUnidadeSaudeIdAndServicoAndStatus(idUnidade, servico, StatusAtendimento.CHAMADO);

        if(!alguemFoiChamado){
            atendimentoRepository.findFirstByUnidadeSaudeIdAndServicoAndStatusOrderByDataHoraChegadaAscIdAsc(
                    idUnidade, servico, StatusAtendimento.AGUARDANDO)
                    .ifPresent(proximo ->{
                        proximo.setStatus(StatusAtendimento.CHAMADO);
                        proximo.setDataHoraChamada(LocalDateTime.now());
                        atendimentoRepository.save(proximo);
                    });
        }
    }

    private AtendimentoResponse mapearAtendimentoComPosicaoDaFila(Atendimento atendimento){
        if(atendimento.getStatus() != StatusAtendimento.AGUARDANDO){
            return AtendimentoMapper.toResponse(atendimento, 0);
        }

        int posicaoFila = (int) atendimentoRepository.calcularPosicaoFila(
                atendimento.getUnidadeSaude().getId(),
                atendimento.getServico(),
                atendimento.getDataHoraChegada(),
                atendimento.getId()
        );

        return AtendimentoMapper.toResponse(atendimento, posicaoFila);
    }
}
