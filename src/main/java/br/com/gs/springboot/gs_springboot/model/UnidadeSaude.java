package br.com.gs.springboot.gs_springboot.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_unidade_saude")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UnidadeSaude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_localizacao")
    private Localizacao local;

    private Double tempoEspera;

    @Enumerated(EnumType.STRING)
    private TipoUnidade tipoUnidade;
}
