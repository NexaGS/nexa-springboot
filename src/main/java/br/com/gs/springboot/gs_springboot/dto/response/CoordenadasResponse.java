package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "CoordenadasResponse",
        description = "Par de coordenadas geográficas (latitude e longitude) em graus decimais."
)
public record CoordenadasResponse(
        @Schema(
                description = "Latitude em graus decimais (WGS84).",
                example = "-23.5648865",
                minimum = "-90",
                maximum = "90",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal latitude,

        @Schema(
                description = "Longitude em graus decimais (WGS84).",
                example = "-46.6529992",
                minimum = "-180",
                maximum = "180",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal longitude
) {
}
