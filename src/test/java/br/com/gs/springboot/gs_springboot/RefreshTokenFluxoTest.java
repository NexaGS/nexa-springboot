package br.com.gs.springboot.gs_springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenFluxoTest {

    @Autowired
    private MockMvc mvc;

    private String registrar(String email) throws Exception {
        String corpo = """
                {"nome":"Ana Souza","email":"%s","senha":"SenhaForte@2026",
                 "confirmarSenha":"SenhaForte@2026","aceitouLocalizacao":true}""".formatted(email);
        return mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String campo(String json, String nome) {
        Matcher m = Pattern.compile("\"" + nome + "\":\"([^\"]+)\"").matcher(json);
        assertThat(m.find()).as("campo " + nome).isTrue();
        return m.group(1);
    }

    private String renovar(String refresh, int statusEsperado) throws Exception {
        return mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refresh + "\"}"))
                .andExpect(status().is(statusEsperado))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void registroDevolveAccessERefreshToken() throws Exception {
        String resposta = registrar("a" + UUID.randomUUID() + "@teste.com");

        assertThat(campo(resposta, "token")).contains(".");
        assertThat(campo(resposta, "refreshToken")).isNotBlank();
    }

    @Test
    void refreshTrocaPorUmParNovoEORefreshAntigoNaoServeMais() throws Exception {
        String primeiro = campo(registrar("b" + UUID.randomUUID() + "@teste.com"), "refreshToken");

        String segundo = campo(renovar(primeiro, 200), "refreshToken");

        assertThat(segundo).isNotEqualTo(primeiro);
        renovar(primeiro, 401);
    }

    @Test
    void reusoDeRefreshJaUsadoRevogaASessaoInteira() throws Exception {
        String primeiro = campo(registrar("c" + UUID.randomUUID() + "@teste.com"), "refreshToken");
        String segundo = campo(renovar(primeiro, 200), "refreshToken");

        renovar(primeiro, 401);

        renovar(segundo, 401);
    }

    @Test
    void logoutRevogaARefreshToken() throws Exception {
        String refresh = campo(registrar("d" + UUID.randomUUID() + "@teste.com"), "refreshToken");

        mvc.perform(post("/api/auth/logout").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refresh + "\"}"))
                .andExpect(status().isNoContent());

        renovar(refresh, 401);
    }

    @Test
    void refreshDesconhecidoOuVazio() throws Exception {
        renovar("token-que-nao-existe", 401);
        renovar("", 400);
    }
}
