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
import br.com.gs.springboot.gs_springboot.repository.AtendimentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class AtendimentoServiceImpl implements AtendimentoService {
    private final AtendimentoRepository repository;
    private final AtendimentoModelAssembler atendimentoModelAssembler;
    private final PacienteService pacienteService;


    public AtendimentoServiceImpl(AtendimentoRepository repository, AtendimentoModelAssembler atendimentoModelAssembler, PacienteService pacienteService) {
        this.repository = repository;
        this.atendimentoModelAssembler = atendimentoModelAssembler;
        this.pacienteService = pacienteService;
    }

    @Override
    public Atendimento findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atendimento", id));
    }

    @Override
    public EntityModel<AtendimentoResponse> buscarAtendimento(Long id) {
        Atendimento atendimento = findById(id);
        return atendimentoModelAssembler.toModel(atendimento);
    }

    @Override
    public AtendimentoResponse iniciarAtendimento(AtendimentoCriacaoRequest request) {
        Atendimento atendimento = new Atendimento();
        atendimento.setId(null);
        //Verificar como implementar inserção de unidade de saude para registro

//        atendimento.setColaborador(colaboradorLogado);


        Optional<Paciente> paciente = pacienteService.buscarPorCpf(request.cpf());
        atendimento.setPaciente(paciente);

        return AtendimentoMapper.toResponse(repository.save(atendimento));
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
        return AtendimentoMapper.toResponse(repository.save(atendimento));
    }


    //PARA OS MÉTODOS DE CALCULO DE TEMPO DE ATENDIMENTO, ACHO QUE PODE EXISTIR O TEMPO MEDIO, POREM VEJO QUE EM IDEIA O MVP DEVE COBRIR UMA ESTIMATIVA POR MEIO DA QUANTIDADE DE PESSOAS QUE ESTAO AGUARDADNO NA FILA DE ATENDIMENTOS
}
