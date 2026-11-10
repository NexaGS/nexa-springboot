package br.com.gs.springboot.gs_springboot.repository;

import br.com.gs.springboot.gs_springboot.model.Cidadao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CidadaoRepository extends JpaRepository<Cidadao, Long> {
    Optional<Cidadao> findByEmail(String email);
    boolean existsByEmail(String email);
}
