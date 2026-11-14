package br.com.gs.springboot.gs_springboot.repository;

import br.com.gs.springboot.gs_springboot.model.StatusAtendimento;
import br.com.gs.springboot.gs_springboot.model.UnidadeSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {
    String DISTANCIA_KM = "(6371.0 * acos(least(1.0, "
            + "cos(radians(:latitudeUsuario)) * cos(radians(l.latitude)) * "
            + "cos(radians(l.longitude) - radians(:longitudeUsuario)) + "
            + "sin(radians(:latitudeUsuario)) * sin(radians(l.latitude)))))";

    String FILTRO_CAIXA = "WHERE l.latitude BETWEEN :latMin AND :latMax "
            + "AND l.longitude BETWEEN :lonMin AND :lonMax";

    @Query(value = "SELECT u, " + DISTANCIA_KM + " FROM UnidadeSaude u JOIN FETCH u.local l " + FILTRO_CAIXA
            + " ORDER BY " + DISTANCIA_KM + " ASC, u.tempoEspera ASC",
            countQuery = "SELECT COUNT(u) FROM UnidadeSaude u JOIN u.local l " + FILTRO_CAIXA)
    Page<Object[]> buscarUnidadesProximas(
            @Param("latitudeUsuario") BigDecimal latitudeUsuario,
            @Param("longitudeUsuario") BigDecimal longitudeUsuario,
            @Param("latMin") BigDecimal latitudeMinima,
            @Param("latMax") BigDecimal latitudeMaxima,
            @Param("lonMin") BigDecimal longitudeMinima,
            @Param("lonMax") BigDecimal longitudeMaxima,
            Pageable pageable
    );

    //Ainda verificar se pode ser feito o calculo via banco query no banco ou no backend


}
