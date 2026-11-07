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
        EntityModel<AtendimentoResponse> model = EntityModel.of(AtendimentoMapper.toResponse(atendimento));
        model.add(linkTo(methodOn(AtendimentoController.class).buscarAtendimento(atendimento.getId())).withSelfRel());
        if(atendimento.getStatus() == StatusAtendimento.ABERTO){
            model.add(linkTo(methodOn(AtendimentoController.class).encerrarAtendimento(atendimento.getId(), null)).withRel("encerrar"));
        }
        return model;
    }
}
