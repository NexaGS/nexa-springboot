package br.com.gs.springboot.gs_springboot.assembler;

import br.com.gs.springboot.gs_springboot.controller.UnidadeSaudeController;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.dto.response.UnidadeSaudeResponse;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.mapper.UnidadeSaudeMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UnidadeSaudeModelAssembler implements RepresentationModelAssembler<UnidadeSaude, EntityModel<UnidadeSaudeResponse>> {
    @Override
    public EntityModel<UnidadeSaudeResponse> toModel(UnidadeSaude unidade) {
        EntityModel<UnidadeSaudeResponse> model = EntityModel.of(UnidadeSaudeMapper.toResponse(unidade));
        model.add(linkTo(methodOn(UnidadeSaudeController.class).listarUnidade(unidade.getId())).withSelfRel());
        return model;
    }
}
