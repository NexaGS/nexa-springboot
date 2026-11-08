package br.com.gs.springboot.gs_springboot.client;

import br.com.gs.springboot.gs_springboot.dto.response.ViaCepResponse;
import br.com.gs.springboot.gs_springboot.exception.CepNaoEncontradoException;
import br.com.gs.springboot.gs_springboot.exception.ServicoIndisponivelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ViaCepApiClient {
    private static final Logger log = LoggerFactory.getLogger(ViaCepApiClient.class);
    private final RestClient restClient;

    public ViaCepApiClient(@Qualifier("viaCepRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public ViaCepResponse buscarEndereco(String cep){
        try{
            ViaCepResponse response = restClient.get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    //validacoes
                    .body(ViaCepResponse.class);

            if(response == null){
                throw new CepNaoEncontradoException(cep);
            }
            return response;
        } catch (CepNaoEncontradoException | ServicoIndisponivelException ex) {
            throw ex;
        } catch (RestClientException ex){
            log.error("Falha ao comunicar com API ViaCep, ao inserir o Cep: {}", cep, ex);
            throw new ServicoIndisponivelException();
        }
    }
}
