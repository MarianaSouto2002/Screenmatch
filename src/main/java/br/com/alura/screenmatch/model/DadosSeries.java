package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;//"apelido"
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)//ignora as outras propriedades que eu não estou passando
//ou seja tudo que não for titulo, totalTemporadas e avaliacao ele ignora

public record DadosSeries(@JsonAlias("Title") String titulo,
                          @JsonAlias("totalSeasons") Integer totalTemporadas,
                          @JsonAlias("imdbRating") String avaliacao) {

    //criou um record para representar os dados da serie, não vai precisar trabalhar com essa classe

}
