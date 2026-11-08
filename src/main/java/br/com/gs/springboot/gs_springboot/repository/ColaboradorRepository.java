package br.com.gs.springboot.gs_springboot.repository;

import br.com.gs.springboot.gs_springboot.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    Optional<Colaborador> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Colaborador> findByUnidadeId(Long unidadeId);
}
