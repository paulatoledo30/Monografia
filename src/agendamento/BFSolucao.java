package agendamento;

import interfaces.Instancia;
import interfaces.Solucao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import memoria.Primes;

/**
 * Classe para representar a solucao do problema de Sequenciamento de tarefas em
 * uma unica maquina com data de entrega comum e diferentes penalidades de
 * atraso e adiantamento
 *
 * Como proposto por Biskup e Feldmann
 *
 * @author Maycon Amaro e Paula Toledo
 */
public class BFSolucao implements Solucao {

    /* Atributos envolvidos na representação da solucao */
    public List<Integer> tarefas;

    /* Atributos envolvidos para auxiliar o calculo da funcao objetivo */
    List<Integer> custosAtraso;         // beta
    List<Integer> custosAdiantamento;   // alpha
    List<Integer> atrasos;              // T
    List<Integer> adiantamentos;        // E
    int c;      // acumulador de soma de tempos de processamento
    int tarefa; // registro temporario para tarefa atual
    float soma; // acumulador de soma de custos
    BFInstancia bfins; // registro de instancia

    /**
     * Calcula o valor de função da solução considerando uma instância
     *
     * @param ins Instancia do problema a se considerar
     * @return resultado da função objetivo
     */
    @Override
    public float calcularValorDeFuncao(Instancia ins) {

        if (bfins == null) {
            bfins = (BFInstancia) ins;
        }

        // Garantindo que há uma instância valida
        assert bfins != null;

        if (custosAtraso == null) {
            custosAtraso = new ArrayList();
            for (int i = 0; i < bfins.numeroTarefas; i++) {
                custosAtraso.add(-1);
            }
        }

        if (custosAdiantamento == null) {
            custosAdiantamento = new ArrayList();
            for (int i = 0; i < bfins.numeroTarefas; i++) {
                custosAdiantamento.add(-1);
            }
        }

        if (atrasos == null) {
            atrasos = new ArrayList(bfins.numeroTarefas);
            for (int i = 0; i < bfins.numeroTarefas; i++) {
                atrasos.add(-1);
            }
        }

        if (adiantamentos == null) {
            adiantamentos = new ArrayList(bfins.numeroTarefas);
            for (int i = 0; i < bfins.numeroTarefas; i++) {
                adiantamentos.add(-1);
            }
        }

        c = 0;

        for (int i = 0; i < tarefas.size(); i++) {

            tarefa = tarefas.get(i);

            c += bfins.temposProcessamento.get(tarefa);

            custosAdiantamento.set(i, bfins.custosAdiantamento.get(tarefa));
            custosAtraso.set(i, bfins.custosAtraso.get(tarefa));
            atrasos.set(i, Math.max(c - bfins.dataEntrega, 0));
            adiantamentos.set(i, Math.max(bfins.dataEntrega - c, 0));
        }

        soma = 0;

        for (int i = 0; i < tarefas.size(); i++) {

            soma += adiantamentos.get(i) * custosAdiantamento.get(i) + atrasos.get(i) * custosAtraso.get(i);
        }

        // Garantindo que a função objetivo foi calculada (não garante que o valor está certo)
        assert soma != 0;

        return soma;
    }

    /**
     * Criador de cópia
     *
     * @return uma cópia desse objeto
     */
    @Override
    public Solucao retornarCopia() {
        BFSolucao s = new BFSolucao();
        s.tarefas = new ArrayList(this.tarefas);
        s.bfins = this.bfins;
        return s;
    }

    @Override
    public void gerarSolucaoAleatoria(Instancia i) {

        BFInstancia bf = (BFInstancia) i;
        List<Integer> tar = new ArrayList<>();

        for (int j = 0; j < bf.numeroTarefas; j++) {

            tar.add(j);
        }

        Collections.shuffle(tar, new Random(100));
        tarefas = tar;
        bfins = bf;
    }

    @Override
    public boolean equals(Object obj) {
        BFSolucao bf = (BFSolucao) obj;

        for (int i = 0; i < this.tarefas.size(); i++) {
            if (!bf.tarefas.get(i).equals(this.tarefas.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {

        int sum = 0;
        int i = 0;
        List<Integer> primos = Primes.gerarPrimos();

        for (int j : this.tarefas) {
            sum += j * primos.get(i++);
        }

        return sum;
    }

    @Override
    public void perturbar(int movimento, Random rand, int tamBloco) {
        int i, j;
        switch (movimento) {

            case 1:
                //TROCA ADJACENTE
                i = rand.nextInt(tarefas.size());
                j = (i == tarefas.size() - 1 ? 0 : i + 1);

                int aux = tarefas.get(i);
                tarefas.set(i, tarefas.get(j));
                tarefas.set(j, aux);
                break;
            case 2:
                //TROCA ALEATÓRIA
                i = rand.nextInt(tarefas.size());
                j = i;
                while (i == j) {
                    j = rand.nextInt(tarefas.size());
                }

                int aux1 = tarefas.get(i);
                tarefas.set(i, tarefas.get(j));
                tarefas.set(j, aux1);
                break;

            // BUSCA POR BLOCO - PAULA
            case 3:
                //System.out.println("TROCA BLOCOS ADJACENTES 3");
                //TROCA BLOCOS ADJACENTES
                //System.out.println("Tamanho Bloco " + tamBloco);
                int qtnde_blocos2 = this.tarefas.size() / tamBloco;
                int quantidadeTotalBlocos = qtnde_blocos2 + 1;
                int tamUltimoBloco = 0;
                //System.out.println("Quantidade: "+qtnde_blocos2);
                int bloco3 = rand.nextInt(qtnde_blocos2);
                int bloco4 = (bloco3 == tarefas.size() - 1 ? 0 : bloco3 + 1);

                List<Integer> novas_tarefas1 = new ArrayList<>(tarefas);

                int inicioBloco3;
                int inicioBloco4;
                int fimBloco3 = 0;
                int fimBloco4 = 0;
                int inicioBlocoAuxiliar = 0;
                int marcadorMaior = 0;
                
                inicioBloco3 = tamBloco * bloco3;
                inicioBloco4 = tamBloco * bloco4;
                //SE O INICIO DO BLOCO FOR A ULTIMA POSICAO DO VETOR TENHO QUE TROCAR COM O PRIMEIRO BLOCO
                if (inicioBloco4 == tarefas.size()) {
                    inicioBloco4 = 0;
                }

                tamUltimoBloco = this.tarefas.size() % tamBloco;
                if (tamUltimoBloco != 0 && (bloco3 == (quantidadeTotalBlocos - 1) || bloco4 == (quantidadeTotalBlocos - 1))) {
                    List<Integer> auxiliar1 = new ArrayList<>(tarefas);

                    //VERIFICANDO QUAL EH O BLOCO QUE TEM TAMANHO MENOR
                    //E INICIALIZANDO AS VARIAVEIS DE INICIO E FIM DOS BLOCOS
                    if (bloco3 != qtnde_blocos2) {
                        inicioBloco3 = tamBloco * bloco3;
                        inicioBlocoAuxiliar = inicioBloco3;
                        fimBloco3 = inicioBloco3 + (tamBloco - 1);
                        marcadorMaior = 1;
                    } else if (bloco4 != qtnde_blocos2) {
                        inicioBloco4 = tamBloco * bloco4;
                        inicioBlocoAuxiliar = inicioBloco4;
                        fimBloco4 = inicioBloco4 + (tamBloco - 1);
                        marcadorMaior = 2;
                    } else if (bloco3 == qtnde_blocos2) {
                        inicioBloco3 = tamBloco * bloco3;
                        fimBloco3 = inicioBloco3 + tamUltimoBloco;
                    } else if (bloco4 == qtnde_blocos2) {
                        inicioBloco4 = tamBloco * bloco4;
                        fimBloco4 = inicioBloco4 + (tamUltimoBloco - 1);
                    }

                    //COLOCANDO NO VETOR AUXILIAR O BLOCO QUE TEM TAMANHO == TAMBLOCO  
                    for (int m = 0; m < tamBloco; m++) {
                        auxiliar1.set(m, this.tarefas.get(inicioBlocoAuxiliar + m));
                    }

                    //TROCANDO NO VETOR AS POSICOES DO BLOCO3 E BLOCO4 SEM REARRANJAR
                    for (int i1 = 0; i1 < tamUltimoBloco; i1++) {
                        this.tarefas.set(inicioBloco3 + i1, novas_tarefas1.get(inicioBloco4 + i1));
                        this.tarefas.set(inicioBloco4 + i1, novas_tarefas1.get(inicioBloco3 + i1));
                    }
                    
                    int n = 0;
                    if (marcadorMaior == 1) {
                        n = fimBloco3 - (tamBloco - tamUltimoBloco - 1);
                    } else if (marcadorMaior == 2) {
                        n = fimBloco4 - (tamBloco - tamUltimoBloco - 1);
                    }

                    //REARRANJANDO O VETOR TAREFAS COM OS BLOCOS TROCADOS CORRETAMENTE
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.tarefas.set(n, auxiliar1.get(i1));
                        n++;
                    }

                } else {
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.tarefas.set(inicioBloco3 + i1, novas_tarefas1.get(inicioBloco4 + i1));
                        this.tarefas.set(inicioBloco4 + i1, novas_tarefas1.get(inicioBloco3 + i1));
                    }
                }

                break;
            case 4: 
                //System.out.println("TROCA BLOCOS ALEATÓRIOS 4");
                //TROCA BLOCOS ALEATÓRIOS
                int qtnde_blocos = this.tarefas.size() / tamBloco;
                int quantidadeTotalBlocos2 = qtnde_blocos + 1;
                int tamUltimoBloco2 = 0;

                int bloco1 = rand.nextInt(qtnde_blocos);
                int bloco2 = bloco1;
 
                while (bloco1 == bloco2) {
                    bloco2 = rand.nextInt(qtnde_blocos);
                }
                //System.out.println("Bloco 3 "+bloco1);
                //System.out.println("Bloco 4 "+bloco2);
                List<Integer> novas_tarefas = new ArrayList<>(tarefas);
                
                int inicioBloco1;
                int inicioBloco2;
                int fimBloco1 = 0;
                int fimBloco2 = 0;
                int inicioBlocoAuxiliar2 = 0;
                int marcadorMaior2 = 0;
                
                inicioBloco1 = tamBloco * bloco1;
                inicioBloco2 = tamBloco * bloco2;
                
                tamUltimoBloco2 = this.tarefas.size() % tamBloco;
                if (tamUltimoBloco2 != 0 && (bloco1 == (quantidadeTotalBlocos2 - 1) || bloco2 == (quantidadeTotalBlocos2 - 1))) {
                    List<Integer> auxiliar1 = new ArrayList<>(tarefas);
                    List<Integer> auxiliar2 = new ArrayList<>(tarefas);
                    
                    //VERIFICANDO QUAL EH O BLOCO QUE TEM TAMANHO MENOR
                    //E INICIALIZANDO AS VARIAVEIS DE INICIO E FIM DOS BLOCOS
                    if (bloco1 != qtnde_blocos) {
                        inicioBloco1 = tamBloco * bloco1;
                        inicioBlocoAuxiliar = inicioBloco1;
                        fimBloco1 = inicioBloco1 + (tamBloco - 1);
                        marcadorMaior2 = 1;
                    } else if (bloco2 != qtnde_blocos) {
                        inicioBloco2 = tamBloco * bloco2;
                        inicioBlocoAuxiliar = inicioBloco2;
                        fimBloco2 = inicioBloco2 + (tamBloco - 1);
                        marcadorMaior2 = 2;
                    } else if (bloco1 == qtnde_blocos) {
                        inicioBloco1 = tamBloco * bloco1;
                        fimBloco1 = inicioBloco1 + tamUltimoBloco2;
                    } else if (bloco2 == qtnde_blocos) {
                        inicioBloco2 = tamBloco * bloco2;
                        fimBloco2 = inicioBloco2 + (tamUltimoBloco2 - 1);
                    }
                    
                    //COLOCANDO NO VETOR AUXILIAR O BLOCO QUE TEM TAMANHO == TAMBLOCO  
                    for (int m = 0; m < tamBloco; m++) {
                        auxiliar1.set(m, this.tarefas.get(inicioBlocoAuxiliar2 + m));
                    }

                    //TROCANDO NO VETOR AS POSICOES DO BLOCO1 E BLOCO2 SEM REARRANJAR
                    for (int i1 = 0; i1 < tamUltimoBloco2; i1++) {
                        this.tarefas.set(inicioBloco1 + i1, novas_tarefas.get(inicioBloco2 + i1));
                        this.tarefas.set(inicioBloco2 + i1, novas_tarefas.get(inicioBloco1 + i1));
                    }
                    
                    int k = 0;
                    int n = 0;
                    int posicaoInicial = 0;
                    if(marcadorMaior2 == 1){
                        k = fimBloco1 + 1;
                        //n = fimBloco1 - (tamBloco - tamUltimoBloco - 1);
                        n = fimBloco2 - (tamBloco - tamUltimoBloco2 - 1);
                        posicaoInicial = fimBloco1;
                    }else if(marcadorMaior2 == 2){
                        k = fimBloco2+1;
                        n = fimBloco1 - (tamBloco - tamUltimoBloco2 - 1);
                        posicaoInicial = fimBloco2;
                    }
                    
                    //COLOCANDO DA POSICAO FINAL DO MAIOR BLOCO +1 EM UM VETOR AUXILIAR
                    int a= 0;
                    for(int l = k; l < this.tarefas.size(); l++){
                        auxiliar2.set(a, this.tarefas.get(l + a));
                        a++;
                    }
                    
                    //COLOCANDO TUDO DO VETOR AUXILIAR NO VETOR TAREFAS
                    for(int i2 = 0; i2 < auxiliar2.size(); i2++){
                       this.tarefas.set(posicaoInicial, auxiliar2.get(i2));
                       posicaoInicial++;
                    }
                    
                    //REARRANJANDO O VETOR TAREFAS COM OS BLOCOS TROCADOS CORRETAMENTE
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.tarefas.set(n, auxiliar1.get(i1));
                        n++;
                    }
                    
                } else {
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.tarefas.set(inicioBloco1 + i1, novas_tarefas.get(inicioBloco2 + i1));
                        this.tarefas.set(inicioBloco2 + i1, novas_tarefas.get(inicioBloco1 + i1));
                    }
                }
                break;
          case 5: 
                //System.out.println("TROCA DE 3 POSIÇÕES BLOCOS ALEATÓRIOS 5");
                //TROCA DE 3 POSIÇÕES BLOCOS ALEATÓRIOS
                int qtnde_blocos3 = this.tarefas.size() / tamBloco;
                int quantidadeTotalBlocos3 = qtnde_blocos3 + 1;
                int tamUltimoBloco3 = 0;
                
                int bloco5 = rand.nextInt(qtnde_blocos3);
                int bloco6 = bloco5;
                int bloco7 = bloco5;
 
                while (bloco5 == bloco6 || bloco5 == bloco7 || bloco6 == bloco7 ) {
                    if(bloco5 == bloco6){
                        bloco6 = rand.nextInt(qtnde_blocos3);
                    }
                    if(bloco5 == bloco7){
                        bloco7 = rand.nextInt(qtnde_blocos3);
                    }
                    if(bloco6 == bloco7){
                        bloco7 = rand.nextInt(qtnde_blocos3);
                    }                    
                }
                //System.out.println("Passou while");
                List<Integer> novas_tarefas3 = new ArrayList<>(tarefas);
                
                int inicioBloco5;
                int inicioBloco6;
                int inicioBloco7;
                int fimBloco5 = 0;
                int fimBloco6 = 0;
                int fimBloco7 = 0;
                int inicioBlocoAuxiliar3 = 0;
                int marcadorMaior3 = 0;
                
                inicioBloco5 = tamBloco * bloco5;
                inicioBloco6 = tamBloco * bloco6;
                inicioBloco7 = tamBloco * bloco7;
                
                tamUltimoBloco3 = this.tarefas.size() % tamBloco;
                if (tamUltimoBloco3 != 0 && (bloco5 == (quantidadeTotalBlocos3 - 1) || bloco6 == (quantidadeTotalBlocos3 - 1) || bloco7 == (quantidadeTotalBlocos3 - 1))) {
                    //System.out.println("entrou if");
                    List<Integer> auxiliar1 = new ArrayList<>(tarefas);
                    List<Integer> auxiliar2 = new ArrayList<>(tarefas);
                    
                    //VERIFICANDO QUAL EH O BLOCO QUE TEM TAMANHO MENOR
                    //E INICIALIZANDO AS VARIAVEIS DE INICIO E FIM DOS BLOCOS
                    if (bloco5 != qtnde_blocos3) {
                        inicioBloco5 = tamBloco * bloco5;
                        inicioBlocoAuxiliar3 = inicioBloco5;
                        fimBloco5 = inicioBloco5 + (tamBloco - 1);
                        marcadorMaior3 = 1;
                    } else if (bloco6 != qtnde_blocos3) {
                        inicioBloco6 = tamBloco * bloco6;
                        inicioBlocoAuxiliar3 = inicioBloco6;
                        fimBloco6 = inicioBloco6 + (tamBloco - 1);
                        marcadorMaior3 = 2;
                    } else if (bloco7 != qtnde_blocos3) {
                        inicioBloco7 = tamBloco * bloco7;
                        inicioBlocoAuxiliar3 = inicioBloco7;
                        fimBloco7 = inicioBloco7 + (tamBloco - 1);
                        marcadorMaior3 = 3;
                    } else if (bloco5 == qtnde_blocos3) {
                        inicioBloco5 = tamBloco * bloco5;
                        fimBloco5 = inicioBloco5 + (tamUltimoBloco3 - 1);
                    } else if (bloco6 == qtnde_blocos3) {
                        inicioBloco6 = tamBloco * bloco6;
                        fimBloco6 = inicioBloco6 + (tamUltimoBloco3 - 1);
                    } else if (bloco7 == qtnde_blocos3) {
                        inicioBloco7 = tamBloco * bloco7;
                        fimBloco7 = inicioBloco7 + (tamUltimoBloco3 - 1);
                    }
                    
                    //COLOCANDO NO VETOR AUXILIAR O BLOCO QUE TEM TAMANHO == TAMBLOCO  
                    for (int m = 0; m < tamBloco; m++) {
                        auxiliar1.set(m, this.tarefas.get(inicioBlocoAuxiliar3 + m));
                    }

                    //TROCANDO NO VETOR AS POSICOES DO BLOCO5, BLOCO6 E BLOCO7 SEM REARRANJAR
                    for (int i1 = 0; i1 < tamUltimoBloco3; i1++) {
                        this.tarefas.set(inicioBloco5 + i1, novas_tarefas3.get(inicioBloco6 + i1));
                        this.tarefas.set(inicioBloco6 + i1, novas_tarefas3.get(inicioBloco7 + i1));
                        this.tarefas.set(inicioBloco7 + i1, novas_tarefas3.get(inicioBloco5 + i1));
                    }
                    
                    int k = 0;
                    int n = 0;
                    int posicaoInicial = 0;
                    if(marcadorMaior3 == 1){
                        k = fimBloco5 + 1;
                        n = fimBloco6 - (tamBloco - tamUltimoBloco3 - 1);
                        posicaoInicial = fimBloco6;
                    }else if(marcadorMaior3 == 2){
                        k = fimBloco6+1;
                        n = fimBloco7 - (tamBloco - tamUltimoBloco3 - 1);
                        posicaoInicial = fimBloco6;
                    }else if(marcadorMaior3 == 3){
                        k = fimBloco7+1;
                        n = fimBloco6 - (tamBloco - tamUltimoBloco3 - 1);
                        posicaoInicial = fimBloco6;
                    }
                    
                    
                    //COLOCANDO A POSICAO FINAL DO MAIOR BLOCO +1 EM UM VETOR AUXILIAR
                    int a= 0;
                    for(int l = k; l < this.tarefas.size(); l++){
                        auxiliar2.set(a, this.tarefas.get(l + a));
                        a++;
                    }
                    
                    //COLOCANDO TUDO DO VETOR AUXILIAR NO VETOR TAREFAS
                    for(int i2 = 0; i2 < auxiliar2.size(); i2++){
                       this.tarefas.set(posicaoInicial, auxiliar2.get(i2));
                       posicaoInicial++;
                    }
                    
                    //REARRANJANDO O VETOR TAREFAS COM OS BLOCOS TROCADOS CORRETAMENTE
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.tarefas.set(n, auxiliar1.get(i1));
                        n++;
                    }
                   
                } else {
                   // System.out.println("entrou else");

                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.tarefas.set(inicioBloco5 + i1, novas_tarefas3.get(inicioBloco6 + i1));
                        this.tarefas.set(inicioBloco6 + i1, novas_tarefas3.get(inicioBloco7 + i1));
                        this.tarefas.set(inicioBloco7 + i1, novas_tarefas3.get(inicioBloco5 + i1));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {

        String resp = "";

        resp = tarefas.stream().map((pos) -> String.valueOf(pos)).reduce(resp, String::concat);

        return resp;
    }

}
