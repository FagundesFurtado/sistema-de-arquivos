package controller;

import Abstracoes.DiretórioAtual;
import Abstracoes.GerenciamentoEspaco;
import Abstracoes.Ponteiros;
import hardware.HardDisk;
import java.util.ArrayList;
import java.util.Date;

public class Diretorio implements Cloneable {

    private String nome;
    private Diretorio pai;
    private ArrayList<Diretorio> filhos;
    private int posicaoFilhos;
    private int posicaoPai;

    private ArrayList<Arquivos> arquivos;
    private String data;
    private String permissao;
    private int atual;

    public Diretorio(Diretorio pai, String nome) {
        this.pai = pai;
        this.nome = nome;
        this.filhos = new ArrayList<>();
        this.arquivos = new ArrayList<>();
        Date d = new Date();
        this.data = d.toLocaleString();
        this.permissao = "-rx-r--r--";
        atual = GerenciamentoEspaco.getInstance().getPosicaoLivre();

        if (nome.equals("/")) {

            this.posicaoPai = 0;
        } else {
            this.posicaoPai = pai.getAtual();
        }

        // int atual = GerenciamentoEspaco.getInstance().getPosicaoLivre();
        HardDisk.getInstance().escreveBloco(gerarBinario(), atual);
        criaPonteiroFilho();

    }

    public void addFilhos(Diretorio pai, String nome) {
        Diretorio novo = new Diretorio(pai, nome);
        filhos.add(novo);
        addFilhosBinario(novo);
    }

    public void addFilhosBinario(Diretorio filho) {
        Ponteiros p = new Ponteiros(posicaoFilhos);
        p.abstrair(HardDisk.getInstance().returnBloco(posicaoFilhos));
         p.adicionaFilho(filho.getAtual());

    }

    public void addArquivos(Arquivos arq) {
        arquivos.add(arq);
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    public ArrayList<Diretorio> getFilhos() {
        return filhos;
    }

    public void setFilhos(ArrayList<Diretorio> filhos) {
        this.filhos = filhos;
    }

    public ArrayList<Arquivos> getArquivos() {
        return arquivos;
    }

    public void setArquivos(ArrayList<Arquivos> arquivos) {
        this.arquivos = arquivos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataModificacao() {
        return getData();
    }

    public void setDataModificacao(String dataModificacao) {
        this.setData(dataModificacao);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    public Diretorio clone() throws CloneNotSupportedException {
        return (Diretorio) super.clone();
    }

    public Diretorio(Diretorio pai, String nome, String permissao, String data) {
        this.pai = pai;
        this.nome = nome;
        this.filhos = new ArrayList<>();
        this.arquivos = new ArrayList<>();
        this.data = data;
        this.permissao = permissao;

    }

    public String gerarBinario() {

        StringBuilder abstracao = new StringBuilder();

        abstracao.append("00");

        String nomeBinario = "";

        if (this.nome.length() < 23) {

            for (int i = 0; i < this.nome.length(); i++) {
                nomeBinario += Integer.toBinaryString(0x100 | (int) this.nome.charAt(i)).substring(1);
            }
        } else {
            for (int i = 0; i < 23; i++) {
                nomeBinario += Integer.toBinaryString(0x100 | (int) this.nome.charAt(i)).substring(1);
            }
        }

        while (nomeBinario.length() < 184) {
            nomeBinario += "0";
        }

        abstracao.append(nomeBinario);

        String dataAuxiliar = data;

        String dia = Integer.toBinaryString(Integer.parseInt(dataAuxiliar.substring(0, 2)));
        String mes = Integer.toBinaryString(Integer.parseInt(dataAuxiliar.substring(3, 5)));
        String ano = Integer.toBinaryString(Integer.parseInt(dataAuxiliar.substring(8, 10)) - 17);

        String hora = Integer.toBinaryString(Integer.parseInt(dataAuxiliar.substring(11, 13)));
        String minuto = Integer.toBinaryString(Integer.parseInt(dataAuxiliar.substring(14, 16)));
        String segundo = Integer.toBinaryString(Integer.parseInt(dataAuxiliar.substring(17, 19)));

        String teste = completaBinario(dia, 5) + completaBinario(mes, 4) + completaBinario(ano, 3) + completaBinario(hora, 5) + completaBinario(minuto, 6) + completaBinario(segundo, 6);

        abstracao.append(completaBinario(dia, 5));
        abstracao.append(completaBinario(mes, 4));
        abstracao.append(completaBinario(ano, 3));
        abstracao.append(completaBinario(hora, 5));
        abstracao.append(completaBinario(minuto, 6));
        abstracao.append(completaBinario(segundo, 6));

        String auxiliarPermissao = "";

        for (int i = 1; i < 10; i++) {
            if (this.permissao.charAt(i) == '-') {
                auxiliarPermissao += "0";
            } else {
                auxiliarPermissao += "1";
            }
        }

        abstracao.append(auxiliarPermissao);

        String paiBinario = Integer.toBinaryString(getPosicaoPai());
        while (paiBinario.length() < 16) {
            paiBinario = "0" + paiBinario;
        }
        abstracao.append(paiBinario);
        setPosicaoFilhos(GerenciamentoEspaco.getInstance().getPosicaoLivre());

        String filhosBinario = Integer.toBinaryString(getPosicaoFilhos());

        while (filhosBinario.length() < 16) {
            filhosBinario = "0" + filhosBinario;
        }
        abstracao.append(filhosBinario);
         DiretórioAtual d = new DiretórioAtual();
        d.abstrair(abstracao.toString());
        return abstracao.toString();

    }

    private void criaPonteiroFilho() {
        Ponteiros p = new Ponteiros(posicaoFilhos);

        if (!nome.equals("/")) {
            posicaoPai = pai.getAtual();
        }
        p.setPai(getPosicaoPai());

        HardDisk.getInstance().escreveBloco(p.gerarBinario(), getPosicaoFilhos());

    }

    private String completaBinario(String s, int tamanho) {

        while (s.length() < tamanho) {
            s = "0" + s;
        }

        return s;

    }

    /**
     * @return the posicaoFilhos
     */
    public int getPosicaoFilhos() {
        return posicaoFilhos;
    }

    /**
     * @param posicaoFilhos the posicaoFilhos to set
     */
    public void setPosicaoFilhos(int posicaoFilhos) {
        this.posicaoFilhos = posicaoFilhos;
    }

    /**
     * @return the posicaoPai
     */
    public int getPosicaoPai() {
        return posicaoPai;
    }

    /**
     * @param posicaoPai the posicaoPai to set
     */
    public void setPosicaoPai(int posicaoPai) {
        this.posicaoPai = posicaoPai;
    }

    /**
     * @return the atual
     */
    public int getAtual() {
        return atual;
    }

    /**
     * @param atual the atual to set
     */
    public void setAtual(int atual) {
        this.atual = atual;
    }

}
