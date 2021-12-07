package caixeiro;

import interfaces.Instancia;
import interfaces.Solucao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import memoria.Primes;

/**
 *
 * Classe para representar a solucao do problema do caixeiro viajante
 *
 * @author Maycon Amaro e Paula Toledo
 */
public class TSSolucao implements Solucao {

    /* Atributos envolvidos na representação da solucao */
    public List<Integer> cidades;
    float soma; // acumulador de soma de custos
    TSInstancia tsins; // registro de instancia

    @Override
    public float calcularValorDeFuncao(Instancia ins) {
        if (tsins == null) {
            tsins = (TSInstancia) ins;
        }
        // Garantindo que há uma instância valida
        assert tsins != null;
        soma = 0;
        int tam = cidades.size();
        //System.out.println("tamanho " + tam);
        for (int i = 0; i < tam; i++) {
            if (i != (tam - 1)) {
                soma += tsins.distancia.get(cidades.get(i) - 1).get(cidades.get(i + 1) - 1);
            } else {
                soma += tsins.distancia.get(cidades.get(i) - 1).get(cidades.get(0) - 1);
            }
        }

        assert (soma != 0);
        return soma;
    }

    @Override
    public Solucao retornarCopia() {
        TSSolucao s = new TSSolucao();
        s.cidades = new ArrayList(this.cidades);
        s.tsins = this.tsins;
        return s;
    }

    @Override
    public void gerarSolucaoAleatoria(Instancia i) {
        TSInstancia ti = (TSInstancia) i;
        List<Integer> cid = new ArrayList<>();

        for (int j = 1; j <= ti.numeroCidades; j++) {

            cid.add(j);
        }

        Collections.shuffle(cid, new Random(100));
        cidades = cid;
        tsins = ti;
    }

    @Override
    public void perturbar(int movimento, Random rand, int tamBloco) {
        int i, j;

        switch (movimento) {
            case 1:
                i = rand.nextInt(cidades.size());
                j = (i == cidades.size() - 1 ? 0 : i + 1);

                int aux = cidades.get(i);
                cidades.set(i, cidades.get(j));
                cidades.set(j, aux);
            break;
            case 2:
                i = rand.nextInt(cidades.size());
                j = i;
                while (i == j) {
                    j = rand.nextInt(cidades.size());
                }

                int aux1 = cidades.get(i);
                cidades.set(i, cidades.get(j));
                cidades.set(j, aux1);
            break;
            // BUSCA POR BLOCO - PAULA
            case 3:
                //TROCA BLOCOS ADJACENTES
                int qtnde_blocos2 = this.cidades.size() / tamBloco;
                int quantidadeTotalBlocos = qtnde_blocos2 + 1;
                int tamUltimoBloco = 0;
                int bloco3 = rand.nextInt(qtnde_blocos2);
                int bloco4 = (bloco3 == cidades.size() - 1 ? 0 : bloco3 + 1);
                List<Integer> novas_cidades1 = new ArrayList<>(cidades);
                int inicioBloco3;
                int inicioBloco4;
                int fimBloco3 = 0;
                int fimBloco4 = 0;
                int inicioBlocoAuxiliar = 0;
                int marcadorMaior = 0;
                
                inicioBloco3 = tamBloco * bloco3;
                inicioBloco4 = tamBloco * bloco4;
                //SE O INICIO DO BLOCO FOR A ULTIMA POSICAO DO VETOR TENHO QUE TROCAR COM O PRIMEIRO BLOCO
                if (inicioBloco4 == cidades.size()) {
                    inicioBloco4 = 0;
                }

                tamUltimoBloco = this.cidades.size() % tamBloco;
                if (tamUltimoBloco != 0 && (bloco3 == (quantidadeTotalBlocos - 1) || bloco4 == (quantidadeTotalBlocos - 1))) {
                    List<Integer> auxiliar1 = new ArrayList<>(cidades);

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
                        auxiliar1.set(m, this.cidades.get(inicioBlocoAuxiliar + m));
                    }

                    //TROCANDO NO VETOR AS POSICOES DO BLOCO3 E BLOCO4 SEM REARRANJAR
                    for (int i1 = 0; i1 < tamUltimoBloco; i1++) {
                        this.cidades.set(inicioBloco3 + i1, novas_cidades1.get(inicioBloco4 + i1));
                        this.cidades.set(inicioBloco4 + i1, novas_cidades1.get(inicioBloco3 + i1));
                    }

                    int n = 0;
                    if (marcadorMaior == 1) {
                        n = fimBloco3 - (tamBloco - tamUltimoBloco - 1);
                    } else if (marcadorMaior == 2) {
                        n = fimBloco4 - (tamBloco - tamUltimoBloco - 1);
                    }

                    //REARRANJANDO O VETOR cidades COM OS BLOCOS TROCADOS CORRETAMENTE
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.cidades.set(n, auxiliar1.get(i1));
                        n++;
                    }

                } else {
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.cidades.set(inicioBloco3 + i1, novas_cidades1.get(inicioBloco4 + i1));
                        this.cidades.set(inicioBloco4 + i1, novas_cidades1.get(inicioBloco3 + i1));
                    }
                }            

            break;
            case 4:
                //TROCA BLOCOS ALEATÓRIOS
                int qtnde_blocos = this.cidades.size() / tamBloco;
                int quantidadeTotalBlocos2 = qtnde_blocos + 1;
                int tamUltimoBloco2 = 0;
                int bloco1 = rand.nextInt(qtnde_blocos);
                int bloco2 = bloco1;

                while (bloco1 == bloco2) {
                    bloco2 = rand.nextInt(qtnde_blocos);
                }

                List<Integer> novas_cidades = new ArrayList<>(cidades);
                int inicioBloco1;
                int inicioBloco2;
                int fimBloco1 = 0;
                int fimBloco2 = 0;
                int inicioBlocoAuxiliar2 = 0;
                int marcadorMaior2 = 0;
                
                inicioBloco1 = tamBloco * bloco1;
                inicioBloco2 = tamBloco * bloco2;
                tamUltimoBloco2 = this.cidades.size() % tamBloco;
                if (tamUltimoBloco2 != 0 && (bloco1 == (quantidadeTotalBlocos2 - 1) || bloco2 == (quantidadeTotalBlocos2 - 1))) {
                    List<Integer> auxiliar1 = new ArrayList<>(cidades);
                    List<Integer> auxiliar2 = new ArrayList<>(cidades);
                    
                    //VERIFICANDO QUAL EH O BLOCO QUE TEM TAMANHO MENOR
                    //E INICIALIZANDO AS VARIAVEIS DE INICIO E FIM DOS BLOCOS
                    if (bloco1 != qtnde_blocos) {
                        inicioBloco1 = tamBloco * bloco1;
                        inicioBlocoAuxiliar2 = inicioBloco1;
                        fimBloco1 = inicioBloco1 + (tamBloco - 1);
                        marcadorMaior2 = 1;
                    } else if (bloco2 != qtnde_blocos) {
                        inicioBloco2 = tamBloco * bloco2;
                        inicioBlocoAuxiliar2 = inicioBloco2;
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
                        auxiliar1.set(m, this.cidades.get(inicioBlocoAuxiliar2 + m));
                    }

                    //TROCANDO NO VETOR AS POSICOES DO BLOCO1 E BLOCO2 SEM REARRANJAR
                    for (int i1 = 0; i1 < tamUltimoBloco2; i1++) {
                        this.cidades.set(inicioBloco1 + i1, novas_cidades.get(inicioBloco2 + i1));
                        this.cidades.set(inicioBloco2 + i1, novas_cidades.get(inicioBloco1 + i1));
                    }
                    
                    int k = 0;
                    int n = 0;
                    int posicaoInicial = 0;
                    if(marcadorMaior2 == 1){
                        k = fimBloco1 + 1;
                        n = fimBloco2 - (tamBloco - tamUltimoBloco2 - 1);
                        posicaoInicial = fimBloco1;
                    }else if(marcadorMaior2 == 2){
                        k = fimBloco2+1;
                        n = fimBloco1 - (tamBloco - tamUltimoBloco2 - 1);
                        posicaoInicial = fimBloco2;
                    }
                    
                    //COLOCANDO DA POSICAO FINAL DO MAIOR BLOCO +1 EM UM VETOR AUXILIAR
                    int a= 0;
                    for(int l = k; l < this.cidades.size(); l++){
                        auxiliar2.set(a, this.cidades.get(l + a));
                        a++;
                    }
                    
                    //COLOCANDO TUDO DO VETOR AUXILIAR NO VETOR TAREFAS
                    for(int i2 = 0; i2 < auxiliar2.size(); i2++){
                       this.cidades.set(posicaoInicial, auxiliar2.get(i2));
                       posicaoInicial++;
                    }
                    
                    //REARRANJANDO O VETOR TAREFAS COM OS BLOCOS TROCADOS CORRETAMENTE
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.cidades.set(n, auxiliar1.get(i1));
                        n++;
                    }
                    
                } else {
                    for (int i1 = 0; i1 < tamBloco; i1++) {
                        this.cidades.set(inicioBloco1 + i1, novas_cidades.get(inicioBloco2 + i1));
                        this.cidades.set(inicioBloco2 + i1, novas_cidades.get(inicioBloco1 + i1));
                    }
                }   
            break;
            default:
                break;
        }
    }

    @Override
    public int hashCode() {
        int sum = 0;
        int i = 0;
        List<Integer> primos = Primes.gerarPrimos();

        for (int j : this.cidades) {
            sum += j * primos.get(i++);
        }

        return sum;
    }

    @Override
    public String toString() {
        String resp = "";

        resp = cidades.stream().map((pos) -> String.valueOf(pos)).reduce(resp, String::concat);

        return resp;
    }

    @Override
    public boolean equals(Object obj) {
        TSSolucao ts = (TSSolucao) obj;

        for (int i = 0; i < this.cidades.size(); i++) {
            if (!ts.cidades.get(i).equals(this.cidades.get(i))) {
                return false;
            }
        }

        return true;
    }
}
