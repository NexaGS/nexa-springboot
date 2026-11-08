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
    @Query("""
        SELECT u FROM UnidadeSaude u
        JOIN FETCH u.local l
        WHERE l.latitude between :latMin and :latMax
            AND l.longitude between :lonMin and :lonMax
        ORDER BY(6371.0 * acos(
                cos(radians( :latitudeUsuario)) * cos(radians(l.latitude)) *
                cos(radians( l.longitude) - radians( :longitudeUsuario)) +
                sin(radians( :latitudeUsuario)) * sin(radians(l.latitude))       
            )
        ) ASC, u.tempoEspera ASC       
        """)
    Page<UnidadeSaude> buscarUnidadesProximas(
            @Param("latitudeUsuario") BigDecimal latitudeUsuario,
            @Param("longitudeUsuario") BigDecimal longitudeUsuario,
            @Param("latMin") BigDecimal latitudeMinima,
            @Param("latMax") BigDecimal latitudeMaxima,
            @Param("lonMin") BigDecimal longitudeMinima,
            @Param("lonMax") BigDecimal longitudeMaxima,
            Pageable pageable
    );

    //Ainda verificar se pode ser feito o calculo via banco query no banco ou no backend

    long countByUnidadeSaudeIdAndStatus(Long unidadeSaudeId, StatusAtendimento status);
}
