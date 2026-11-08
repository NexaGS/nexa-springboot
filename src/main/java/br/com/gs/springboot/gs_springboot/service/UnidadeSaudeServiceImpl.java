package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.assembler.UnidadeSaudeModelAssembler;
import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.UnidadeSaudeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UnidadeSaudeServiceImpl implements UnidadeSaudeService {
    private final UnidadeSaudeRepository repository;
    private final UnidadeSaudeModelAssembler unidadeSaudeAssembler;
    private final GoogleMapsService googleMapsService;

    public UnidadeSaudeServiceImpl(UnidadeSaudeRepository repository, UnidadeSaudeModelAssembler unidadeSaudeAssembler, GoogleMapsService googleMapsService) {
        this.repository = repository;
        this.unidadeSaudeAssembler = unidadeSaudeAssembler;
        this.googleMapsService = googleMapsService;
    }

    @Override
    public UnidadeSaude findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unidade", id));
    }

    @Override
    public Page<UnidadeSaude> listarTodas(Integer pagina, Integer tamanho) {
        return repository.findAll(Pageable.ofSize(tamanho).withPage(pagina));
    }

    @Override
    public EntityModel<UnidadeSaudeResponse> listarUnidade(Long id) {
        UnidadeSaude unidade = findById(id);
        return unidadeSaudeAssembler.toModel(unidade);
    }

    @Override
    public Page<UnidadeSaude> listarUnidadesProximidade(
            BigDecimal latitude,
            BigDecimal longitude,
            String endereco,
            Integer pagina,
            Integer tamanho
    ) {
        CoordenadasResponse coordenadas = googleMapsService.resolverCoordenadas(latitude, longitude, endereco);
        BigDecimal latitudeUsuario = coordenadas.latitude();
        BigDecimal longitudeUsuario = coordenadas.longitude();

        BigDecimal delta = BigDecimal.valueOf(0.20);
        BigDecimal latitudeMinima = latitudeUsuario.subtract(delta);
        BigDecimal latitudeMaxima = latitudeUsuario.add(delta);
        BigDecimal longitudeMinima = longitudeUsuario.subtract(delta);
        BigDecimal longitudeMaxima = longitudeUsuario.add(delta);

        return repository.buscarUnidadesProximas(
                latitudeUsuario,
                longitudeUsuario,
                latitudeMinima,
                latitudeMaxima,
                longitudeMinima,
                longitudeMaxima,
                Pageable.ofSize(tamanho).withPage(pagina));
    }
}
