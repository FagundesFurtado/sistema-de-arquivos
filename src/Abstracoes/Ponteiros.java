package Abstracoes;

import hardware.HardDisk;
import java.util.ArrayList;

public class Ponteiros {

    private int pai;
    boolean continua;
    private int proximo;
    private boolean usados[];
    private int filhos[];
    private int atual;

    public Ponteiros(int atual) {
        usados = new boolean[13];
        filhos = new int[13];
        this.atual = atual;
    }

    public int getPai() {
        return pai;
    }

    public boolean isContinua() {
        return continua;
    }

    public int getProximo() {
        return proximo;
    }

    public ArrayList<String> abstrair(String binario) {

        ArrayList<String> filhos = new ArrayList<>();
        this.pai = Integer.parseInt(binario.substring(2, 18), 2);

        filhos.add(".." + "-" + this.pai);
        for (int i = 18; i < 31; i++) {

            if (binario.charAt(i) == '1') {
                int inicio = (i - 18) * 16 + 31;
                usados[i - 18] = true;
                int posicao = Integer.parseInt(binario.substring(inicio, inicio + 16), 2);

                String teste = HardDisk.getInstance().returnBloco(posicao);

                if (teste.subSequence(0, 2).equals("00")) {

                    DiretórioAtual novo = new DiretórioAtual();
                    novo.abstrair(teste);
                    String resultado = novo.getNome().replaceAll(String.valueOf((char) (0000000000000000)), "") + "-" + posicao + "-" + (i - 18);
                    filhos.add(resultado);

                }
                if (teste.subSequence(0, 2).equals("01")) {
                    Arquivo novo = new Arquivo();
                    novo.abstrair(teste);

                    String resultado = novo.getNome().replaceAll(String.valueOf((char) (0000000000000000)), "") + "-" + posicao + "-" + (i - 18);
                    filhos.add(resultado);
                }

            }
        }
        if (continua) {
            novosFilhos(filhos, proximo);
        }

        return filhos;
    }

    public void novosFilhos(ArrayList<String> filhos, int bloco) {

        Ponteiros prox = new Ponteiros(bloco);

        String binario = HardDisk.getInstance().returnBloco(bloco);

        for (int i = 18; i < 31; i++) {

            if (binario.charAt(i) == '1') {
                int inicio = (i - 18) * 16 + 31;
                prox.getUsados()[i - 18] = true;
                int posicao = Integer.parseInt(binario.substring(inicio, inicio + 16), 2);
                String teste = HardDisk.getInstance().returnBloco(posicao);
                if (teste.subSequence(0, 2).equals("00")) {
                    DiretórioAtual novo = new DiretórioAtual();
                    novo.abstrair(teste);

                    String resultado = novo.getNome().replaceAll(String.valueOf((char) (0000000000000000)), "") + "-" + posicao;
                    filhos.add(resultado);

                }
                if (teste.subSequence(0, 2).equals("01")) {
                    Arquivo novo = new Arquivo();
                    novo.abstrair(teste);
                    filhos.add(novo.getNome() + "-" + prox.getFilhos()[posicao]);
                }

            }
        }

        if (continua) {
            novosFilhos(filhos, prox.getProximo());
        }

    }

    public void setPosicaoUsados(boolean valor, int posicao) {

        usados[posicao] = valor;
        HardDisk.getInstance().escreveBloco(gerarBinario(), atual);
    }

    public boolean[] getUsados() {
        return usados;
    }

    public int[] getFilhos() {
        return filhos;
    }

    public void setPai(int pai) {
        this.pai = pai;
    }

    public String gerarBinario() {

          StringBuilder binario = new StringBuilder();

        binario.append("10");
        String paiBinario = Integer.toBinaryString(pai);
        binario.append(completaBinario(paiBinario, 16));

        String usadosString = "";
        String stringFilhos = "";

        for (int i = 0; i < 13; i++) {
            if (usados[i]) {
                usadosString = usadosString + "1";
                stringFilhos = stringFilhos + completaBinario(Integer.toBinaryString(filhos[i]), 16);
           
            } else {
                usadosString = usadosString + "0";
                stringFilhos = stringFilhos + vazio();
            }

        }

      
        binario.append(usadosString);
        binario.append(stringFilhos);
        if (continua) {
            binario.append("1");
            binario.append(completaBinario(Integer.toBinaryString(GerenciamentoEspaco.getInstance().getPosicaoLivre()), 16));
        } else {
            binario.append("0");
            binario.append(vazio());
        }

        return binario.toString();

    }

    private String vazio() {
        return "0000000000000000";
    }

    private String completaBinario(String s, int tamanho) {

        while (s.length() < tamanho) {
            s = "0" + s;
        }

        return s;

    }

    public String adicionaFilho(int filho) {
        String result = "";
        abstrair(HardDisk.getInstance().returnBloco(atual));
         boolean inseriu = false;
        for (int i = 0; i < 13; i++) {
            if (!usados[i]) {
                usados[i] = true;
                inseriu = true;
                filhos[i] = filho;

             
                HardDisk.getInstance().escrevePonteiro(completaBinario(Integer.toBinaryString(filho), 16), 65536 + (atual * 256) + 31 + (i * 16));
                HardDisk.getInstance().setBitDaPosicao(true, 65536 + (atual * 256) + 18 + i);

                i = 13;
//                HardDisk.getInstance().escrevePonteiro(completaBinario(Integer.toBinaryString(filho), 16), 65536 + (atual * 256) + 31 + i * 16);
            }
        }
        if (!inseriu) {

            System.out.println("TUDO LOTADO");
            result = "Impossivel criar pasta - Sistema cheio";
            if (continua) {
                Ponteiros proximo = new Ponteiros(this.proximo);
                proximo.adicionaFilho(filho);
            } else {
                Ponteiros novoProximo = new Ponteiros(GerenciamentoEspaco.getInstance().getPosicaoLivre());
                continua = true;
                this.proximo = novoProximo.getAtual();
                novoProximo.setPai(pai);
                novoProximo.adicionaFilho(filho);

            }

        }
    

//        if (inseriu) {
//            System.out.println("BINARIO\n" + gerarBinario());
//            System.out.println("EU TO NA PQP " + atual);
//            HardDisk.getInstance().escreveBloco(gerarBinario(), atual);
//            System.out.println("Ja inseri");
//            gerarBinario();
////abstrair(gerarBinario());
//        }
        return result;
    }

    public int getAtual() {
        return atual;
    }

}
