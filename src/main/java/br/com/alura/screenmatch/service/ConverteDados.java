package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.DadosSeries;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverteDados implements IConverteDados{// somente para converter, para qualquer classe que quer representar

    private ObjectMapper mapper = new ObjectMapper();//usado para esssa conversão

    @Override
    public <T> T obterDados(String json, Class<T> classe) {

        try {
            return mapper.readValue(json, classe);//pega o json e a classe e tenta converter
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }

    }
}


