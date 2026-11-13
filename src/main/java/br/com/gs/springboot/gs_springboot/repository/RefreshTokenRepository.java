package br.com.gs.springboot.gs_springboot.repository;

import br.com.gs.springboot.gs_springboot.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revogado = true WHERE t.familia = :familia")
    int revogarFamilia(@Param("familia") String familia);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.email = :email AND t.expiraEm < :agora")
    int apagarExpiradosDe(@Param("email") String email, @Param("agora") Instant agora);
}
