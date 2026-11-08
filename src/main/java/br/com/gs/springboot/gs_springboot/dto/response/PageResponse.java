package br.com.gs.springboot.gs_springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(
        name = "PageResponse",
        description = "Envelope padronizado para respostas paginadas da API."
)
public record PageResponse<T>(
        @Schema(
                description = "Lista de elementos pertencentes à página atual.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        List<T> conteudo,

        @Schema(
                description = "Índice da página atual (baseado em zero).",
                example = "0",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int pagina,

        @Schema(
                description = "Quantidade máxima de registros por página.",
                example = "10",
                minimum = "1",
                maximum = "100",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int tamanho,

        @Schema(
                description = "Total global de registros encontrados na consulta.",
                example = "42",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        long totalElementos,

        @Schema(
                description = "Total de páginas disponíveis para os critérios informados.",
                example = "5",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int totalPaginas,

        @Schema(
                description = "Indica se a página atual é a última disponível (`true` se não houver próxima página).",
                example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        boolean ultimo
) {

    public static <T> PageResponse<T> from(Page<T> pagina) {
        return new PageResponse<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isLast()
        );
    }
}
