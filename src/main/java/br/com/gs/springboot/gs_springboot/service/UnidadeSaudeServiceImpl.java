package br.com.gs.springboot.gs_springboot.service;

import br.com.gs.springboot.gs_springboot.assembler.UnidadeSaudeModelAssembler;
import br.com.gs.springboot.gs_springboot.dto.response.CoordenadasResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.exception.ResourceNotFoundException;
import br.com.gs.springboot.gs_springboot.model.Estado;
import br.com.gs.springboot.gs_springboot.model.Localizacao;
import br.com.gs.springboot.gs_springboot.model.Municipio;
import br.com.gs.springboot.gs_springboot.model.TipoUnidade;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import br.com.gs.springboot.gs_springboot.repository.UnidadeSaudeRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UnidadeSaudeServiceImpl implements UnidadeSaudeService {
    private static final double[] DELTAS_GRAUS = {0.5, 2.0, 90.0};
    private static final double KM_POR_GRAU_MINIMO = 90.0;

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
    public Page<UnidadeSaude> listarTodas(
            String nome,
            TipoUnidade tipoUnidade,
            String uf,
            Integer pagina,
            Integer tamanho) {

        UnidadeSaude unidadeFiltro = new UnidadeSaude();

        unidadeFiltro.setNome(nome == null || nome.isBlank() ? null : nome.trim());
        unidadeFiltro.setTipoUnidade(tipoUnidade);

        if (uf != null && !uf.isBlank()) {
            Estado estado = new Estado();
            estado.setSigla(uf.trim().toUpperCase());
            Municipio municipio = new Municipio();
            municipio.setEstado(estado);
            Localizacao local = new Localizacao();
            local.setMunicipio(municipio);
            unidadeFiltro.setLocal(local);
        }

        ExampleMatcher semelhante = ExampleMatcher.matchingAll()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withMatcher("local.municipio.estado.sigla", ExampleMatcher.GenericPropertyMatchers.exact());

        Pageable paginacao = PageRequest.of(pagina, tamanho, Sort.by("nome", "id"));
        return repository.findAll(Example.of(unidadeFiltro, semelhante), paginacao);
    }

    @Override
    public EntityModel<UnidadeSaudeResponse> listarUnidade(Long id) {
        UnidadeSaude unidade = findById(id);
        return unidadeSaudeAssembler.toModel(unidade);
    }

    @Override
    public Page<UnidadeComDistancia> listarUnidadesProximidade(
            BigDecimal latitude,
            BigDecimal longitude,
            String endereco,
            Integer pagina,
            Integer tamanho
    ) {
        CoordenadasResponse coordenadas = googleMapsService.resolverCoordenadas(latitude, longitude, endereco);
        BigDecimal latitudeUsuario = coordenadas.latitude();
        BigDecimal longitudeUsuario = coordenadas.longitude();
        Pageable paginacao = Pageable.ofSize(tamanho).withPage(pagina);

        Page<Object[]> resultado = null;
        for (double delta : DELTAS_GRAUS) {
            BigDecimal caixa = BigDecimal.valueOf(delta);
            resultado = repository.buscarUnidadesProximas(
                    latitudeUsuario,
                    longitudeUsuario,
                    latitudeUsuario.subtract(caixa),
                    latitudeUsuario.add(caixa),
                    longitudeUsuario.subtract(caixa),
                    longitudeUsuario.add(caixa),
                    paginacao);
            if (paginaCompletaDentroDaCaixa(resultado, tamanho, delta * KM_POR_GRAU_MINIMO)) {
                break;
            }
        }

        return resultado.map(linha -> new UnidadeComDistancia((UnidadeSaude) linha[0], ((Number) linha[1]).doubleValue()));
    }

    private static boolean paginaCompletaDentroDaCaixa(Page<Object[]> pagina, int tamanho, double alcanceKm) {
        if (pagina.getContent().size() < tamanho) {
            return false;
        }
        Object[] ultima = pagina.getContent().getLast();
        return ((Number) ultima[1]).doubleValue() <= alcanceKm;
    }
}
