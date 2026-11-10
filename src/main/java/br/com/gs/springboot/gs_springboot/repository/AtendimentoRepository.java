package br.com.gs.springboot.gs_springboot.repository;

import br.com.gs.springboot.gs_springboot.model.Atendimento;
import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import br.com.gs.springboot.gs_springboot.model.TipoServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    Page<Atendimento> findByUnidadeSaudeIdAndStatusInOrderByDataHoraChegadaAsc(
            Long unidadeSaudeId, List<StatusAtendimento> status, Pageable pageable);
    long countByUnidadeSaudeIdAndStatus(Long unidadeSaudeId, StatusAtendimento status);
    Optional<Atendimento> findFirstByUnidadeSaudeIdAndServicoAndStatusOrderByDataHoraChegadaAscIdAsc(
            Long unidadeId, TipoServico servico, StatusAtendimento status
    );
    long countByUnidadeSaudeIdAndServico(Long unidadeId, TipoServico servico);
    boolean existsByUnidadeSaudeIdAndServicoAndStatus(Long unidadeId, TipoServico servico, StatusAtendimento statusAtendimento);

    @Query("""
            SELECT COUNT(a)
            FROM Atendimento a
            WHERE a.unidadeSaude.id = :unidadeId
               AND a.servico = :servico
               AND a.status = StatusAtendimento.AGUARDANDO
               AND (a.dataHoraChegada < :horaChegada OR (a.dataHoraChegada = :horaChegada AND a.id <= :id))
            """)
    long calcularPosicaoFila(
            @Param("unidadeId") Long unidadeId,
            @Param("servico") TipoServico servico,
            @Param("horaChegada") LocalDateTime horaChegada,
            @Param("id") Long id);
}
