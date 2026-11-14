package br.com.gs.springboot.gs_springboot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_refresh_token")
@Check(constraints = "(id_cidadao is not null and id_colaborador is null) or (id_cidadao is null and id_colaborador is not null)")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cidadao")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cidadao cidadao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Colaborador colaborador;

    @Column(nullable = false, length = 36)
    private String familia;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(nullable = false)
    private boolean usado;

    @Column(nullable = false)
    private boolean revogado;

    public String emailDoDono() {
        return cidadao != null ? cidadao.getEmail() : colaborador.getEmail();
    }
}
