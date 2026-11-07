package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.dto.request.ColaboradorRequest;
import br.com.gs.springboot.gs_springboot.dto.response.ColaboradorResponse;
import br.com.gs.springboot.gs_springboot.exception.EmailRegistradoException;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.mapper.ColaboradorMapper;
import br.com.gs.springboot.gs_springboot.model.Colaborador;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorServiceImpl implements ColaboradorService {
    private final ColaboradorRepository repository;
    private final UnidadeSaudeService unidadeSaudeService;

    public ColaboradorServiceImpl(ColaboradorRepository repository, UnidadeSaudeService unidadeSaudeService) {
        this.repository = repository;
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @Override
    public Colaborador findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Colaborador", id));
    }

//    @Override
//    public List<ColaboradorResponse> listarColaboradoresPorUnidade(Long idUnidade) {
//        return repository.fin;
//    }

    @Override
    public ColaboradorResponse criar(ColaboradorRequest request) {
        if(repository.existsByEmail(request.email())){
            throw new EmailRegistradoException(request.email());
        }

        Colaborador colaborador = new Colaborador();
        colaborador.setId(null);

        colaborador.setNome(request.nome());
        colaborador.setCargo(request.cargo());
        colaborador.setEmail(request.email());

        UnidadeSaude unidade = unidadeSaudeService.findById(request.idUnidadeSaude());
        colaborador.setUnidade(unidade);

        return ColaboradorMapper.toResponse(repository.save(colaborador));
    }
}
