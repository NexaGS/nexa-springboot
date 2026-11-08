package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.assembler.AtendimentoModelAssembler;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoAtualizacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.request.AtendimentoCriacaoRequest;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.exception.InvalidStatusTransitionException;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.model.Paciente;
import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.AtendimentoRepository;
import br.com.gs.springboot.gs_springboot.repository.UnidadeSaudeRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class AtendimentoServiceImpl implements AtendimentoService {
    private final AtendimentoRepository atendimentoRepository;
    private final AtendimentoModelAssembler atendimentoModelAssembler;
    private final PacienteService pacienteService;
    private final UnidadeSaudeRepository unidadeRepository;

    public AtendimentoServiceImpl(AtendimentoRepository atendimentoRepository, AtendimentoModelAssembler atendimentoModelAssembler, PacienteService pacienteService, UnidadeSaudeRepository unidadeRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.atendimentoModelAssembler = atendimentoModelAssembler;
        this.pacienteService = pacienteService;
        this.unidadeRepository = unidadeRepository;
    }

    @Override
    public Atendimento findById(Long id) {
        return atendimentoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atendimento", id));
    }

    @Override
    public EntityModel<AtendimentoResponse> buscarAtendimento(Long id) {
        Atendimento atendimento = findById(id);
        return atendimentoModelAssembler.toModel(atendimento);
    }

    @Override
    public Page<AtendimentoResponse> listarAtendimentosPorUnidade(Long unidadeId, Integer pagina, Integer tamanho){
        return atendimentoRepository.findByUnidadeSaudeIdAndStatus(unidadeId, StatusAtendimento.ABERTO,
                Pageable.ofSize(tamanho).withPage(pagina)).map(AtendimentoMapper::toResponse);
    }

    @Override
    public AtendimentoResponse iniciarAtendimento(AtendimentoCriacaoRequest request) {
        Atendimento atendimento = new Atendimento();

        //Verificar como implementar inserção de unidade de saude para registro

//        atendimento.setColaborador(colaboradorLogado);


        atendimento.setPaciente(pacienteService.buscarPorCpf(request.cpf())
                        .orElseThrow(() -> new ResourceNotFoundException("Paciente", 0L)));

        Atendimento atendimentoSalvo = atendimentoRepository.save(atendimento);
        atualizarTempoEsperaUnidade(atendimento.getUnidadeSaude());

        return AtendimentoMapper.toResponse(atendimentoSalvo);
    }

    @Override
    public AtendimentoResponse encerrarAtendimento(Long id, AtendimentoAtualizacaoRequest request) {
        Atendimento atendimento = findById(id);
        if(atendimento.getStatus() == StatusAtendimento.ENCERRADO){
            throw new InvalidStatusTransitionException("Atendimento já consta encerrado!");
        }

        //Inserir colaborador - poderia ser pela autenticacao feita antes (simulação anterior de login.)
        //Com isso, penso que apenas quem teria login na plataforma nesse MVP seria o colaborador que encerra atendimento. E sem divisão de role entre eles, mas sim para a visão de endpoints.

        atendimento.setStatus(StatusAtendimento.ENCERRADO);
        atendimento.setDataHoraFim(LocalDateTime.now());
        atualizarTempoEsperaUnidade(atendimento.getUnidadeSaude());
        return AtendimentoMapper.toResponse(atendimentoRepository.save(atendimento));
    }

    private void atualizarTempoEsperaUnidade(UnidadeSaude unidade){
        long filaAberta = atendimentoRepository.countByUnidadeSaudeIdAndStatus(unidade.getId(), StatusAtendimento.ABERTO);

        //Estimativa
        double minutosPaciente = 20.0;

        unidade.setTempoEspera(filaAberta * minutosPaciente);
        unidadeRepository.save(unidade);
    }

    //PARA OS MÉTODOS DE CALCULO DE TEMPO DE ATENDIMENTO, ACHO QUE PODE EXISTIR O TEMPO MEDIO, POREM VEJO QUE EM IDEIA O MVP DEVE COBRIR UMA ESTIMATIVA POR MEIO DA QUANTIDADE DE PESSOAS QUE ESTAO AGUARDADNO NA FILA DE ATENDIMENTOS
}
