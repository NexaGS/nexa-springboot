package br.com.gs.springboot.gs_springboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_cidadao")
public class Cidadao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    @Column(name= "aceite_localizacao", nullable = false)
    private LocalDateTime dataHoraAceiteLocalizacao;

    @ManyToMany( fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_cidadao_unidade_saude",
            joinColumns = @JoinColumn(name = "id_cidadao"),
            inverseJoinColumns = @JoinColumn(name = "id_unidade_saude")
    )
    private Set<UnidadeSaude> unidadesFavoritas;

    public void adicionarUnidadeFavorita(UnidadeSaude unidade){
        this.unidadesFavoritas.add(unidade);
    }
    public void removerUnidadeFavorita(UnidadeSaude unidade){
        this.unidadesFavoritas.remove(unidade);
    }
}
