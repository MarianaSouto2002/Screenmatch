package br.com.alura.screenmatch.service;

public interface IConverteDados {//interface que obtem os dados e converte, para qualquer tipo de dados

    //método
    <T> T obterDados(String json, Class <T> classe);//é do tipo T pois vai ser um dado genérico, pode ser qualquer dado, logo qualquer tipo
        //o que esse método recebe
}
