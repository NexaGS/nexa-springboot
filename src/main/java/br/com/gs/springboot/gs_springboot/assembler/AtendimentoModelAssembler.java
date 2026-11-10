package br.com.gs.springboot.gs_springboot.assembler;

import br.com.gs.springboot.gs_springboot.controller.AtendimentoController;
import br.com.gs.springboot.gs_springboot.dto.response.AtendimentoResponse;
import br.com.gs.springboot.gs_springboot.mapper.AtendimentoMapper;
import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AtendimentoModelAssembler implements RepresentationModelAssembler<Atendimento, EntityModel<AtendimentoResponse>> {

    @Override
    public EntityModel<AtendimentoResponse> toModel(Atendimento atendimento) {
        return toModel(AtendimentoMapper.toResponse(atendimento));
    }

    public EntityModel<AtendimentoResponse> toModel(AtendimentoResponse response) {
        EntityModel<AtendimentoResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(AtendimentoController.class).buscarAtendimento(response.id())).withSelfRel());
        if(response.status() == StatusAtendimento.FINALIZADO){
            model.add(linkTo(methodOn(AtendimentoController.class).encerrarAtendimento(response.id(), null)).withRel("encerrar"));
        }
        return model;
    }
}
