package br.com.alura.screenmatch.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoApi {//classe para consumir qualquer API

    public String obterDados(String endereco){//método para obter o endereço

            HttpClient client = HttpClient.newHttpClient();//cliente
            HttpRequest request = HttpRequest.newBuilder()//resposta
                    .uri(URI.create(endereco))
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new RuntimeException(e);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String json = response.body();//o que interessa é o corpo da resposta que vai devolver
            return json;

    }
}
