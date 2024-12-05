package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSeries;
import br.com.alura.screenmatch.model.DadosTemporadas;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private ConsumoApi consumo = new ConsumoApi();//instanciar a classe
    private Scanner leitura = new Scanner(System.in);//para pegar o que foi digitado

    //criação das constantes do endereço:
    //"https://omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=4766e694" as partes fixas são constantes
    private final String ENDERECO = "https://www.omdbapi.com/?t=";//chama eleas de final para dizer que não vão ser modificadas

    private final String API_KEY = "&apikey=4766e694";//deve ser tudo maiusculo

    private ConverteDados conversor = new ConverteDados();


    //método para exibir o menu:
    public void exibirMenu() {
        System.out.println("Digite o nome da série para a busca:");
        var nomeSerie = leitura.nextLine();//pega a String
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);//endereço completo
        //o .replace é para a API entender quando tiver espaço
        ConverteDados converteDados = new ConverteDados();
        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);//instancia o conversor e transforma em dadosSeries
        System.out.println(dados);

        List<DadosTemporadas> temporadas = new ArrayList<>();//criando uma lista de temporadas

		//para ter somente os dados das temporadas
		for (int i = 1; i<= dados.totalTemporadas(); i++){ //o i é a temporada buscada
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") +"&season=" + i +  API_KEY);//indica a temporada e o ep
			DadosTemporadas dadosTemporadas = conversor.obterDados(json, DadosTemporadas.class);
			temporadas.add(dadosTemporadas);
		}
		temporadas.forEach(System.out::println);

        //para imprimir só algumas informações

        //usa uma única letra para declarar o parâmetro, chama ele e faz alguma coisa, é a função lâmbida = ((parametro) -> expressao))
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));//para cada temporada vai percorrer os episódios, e vai imprimir os seus títulos

        //streams é um fluxo de dados, dá para fazer várias operações encadeadas que usa o lâmbida:
        List<DadosEpisodios> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())//uma lista dentro de outra e quero puxar todas as listas juntas
                .collect(Collectors.toList());//coleta tudo para a lista de dadosEpisodios
//        System.out.println("\nTop 10 episódios da série:");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))//aqueles que não possuem avaliação não entram
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))//olhar o código para debugar
//                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())//compara os dados episódios pela avaliação em ordem decrescente
//                .peek(e -> System.out.println("Ordenação: " + e))
//                .limit(10)//limita em 5
//                .peek(e -> System.out.println("limite " + e))
//                .map((e -> e.titulo().toUpperCase()))
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);

        //Lista de episódios
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))//para cada dado episodio cria um new episodio com numero temporada e as informações
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do título do episódio que procura: ");
        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()//optional é um obj container que pode ou não conter um valor nulo
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))//se o título tiver um trecho do nome do título que eu busco, filtra ele
                .findFirst();//pega a primeira referencia que encontrar
        if(episodioBuscado.isPresent()) {//se existe o episodio
            System.out.println("Episódio encontraddo!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());//pega a temporada do episódio que está dentro do Optional
        }else {
            System.out.println("Episódio não encontrado!");
        }


//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//
//        LocalDate dataBusca = LocalDate.of(ano, 1,1);//data com ano, mes e dia
//
//        //formatador da data:
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))//filtra a data, não pode ser nula e tem que ser depois da data que está buscando
//                .forEach(e -> System.out.println("Temporada: " + e.getTemporada() +
//                        " Episódio: " + e.getTitulo() +
//                        " Data lançamento : " + e.getDataLancamento().format(formatador)
//                ));


        //avaliações por temporada, médias por temporadas:
        Map<Integer, Double> avaliacaoePorTemporada = episodios.stream() //de inteiro para double(média)
                .filter(e -> e.getAvaliacao() > 0.0)//pega aqueles que possuem avaliação
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));//averagingDouble faz a média
        System.out.println(avaliacaoePorTemporada);

        //estatistica para relatórios
        DoubleSummaryStatistics est = episodios.stream()//vai colocar algo e vai colocar na DoubleSummaryStatistics
                .filter(e -> e.getAvaliacao() > 0.0)//pega aqueles que possuem avaliação
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));//estatistica da avaliação a contagem, soma, minimo, maximo e média
        System.out.println("Média:  " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade de episódios: " + est.getCount());

    }
}