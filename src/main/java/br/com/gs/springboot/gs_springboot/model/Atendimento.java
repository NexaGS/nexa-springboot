package br.com.gs.springboot.gs_springboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_atendimento")
public class Atendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidade_saude")
    private UnidadeSaude unidadeSaude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador")
    private Colaborador colaborador;

    @Enumerated(EnumType.STRING)
    private StatusAtendimento status = StatusAtendimento.ABERTO;

    private LocalDateTime dataHoraChegada = LocalDateTime.now();
    private LocalDateTime dataHoraFim;
}
