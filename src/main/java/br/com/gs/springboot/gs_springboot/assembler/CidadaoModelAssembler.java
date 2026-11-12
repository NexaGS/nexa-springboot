package br.com.gs.springboot.gs_springboot.assembler;

import br.com.gs.springboot.gs_springboot.controller.CidadaoController;
import br.com.gs.springboot.gs_springboot.dto.response.CidadaoResponse;
import br.com.gs.springboot.gs_springboot.mapper.CidadaoMapper;
import br.com.gs.springboot.gs_springboot.model.Cidadao;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CidadaoModelAssembler implements RepresentationModelAssembler<Cidadao, EntityModel<CidadaoResponse>> {

    @Override
    public EntityModel<CidadaoResponse> toModel(Cidadao cidadao) {
        return toModel(CidadaoMapper.toResponse(cidadao));
    }

    public EntityModel<CidadaoResponse> toModel(CidadaoResponse response) {
        EntityModel<CidadaoResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(CidadaoController.class).obterPerfil()).withSelfRel());
        return model;
    }
}
