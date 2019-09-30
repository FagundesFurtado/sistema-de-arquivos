package Abstracoes;

import controller.Diretorio;
import hardware.HardDisk;
import java.util.ArrayList;
import java.util.Date;

public class DiretórioAtual {

    private String nome;
    private String data;
    private String permissao = "-";
    private int pai;
    private int filhos;
    private int posicaoFilhos;
    private int posicaoPai;
    private int atual;

    public DiretórioAtual() {
        this.nome = "";
        this.data = "";

    }

    public DiretórioAtual(String nome, int pai) {
        this.pai = pai;
        this.nome = nome;

        Date d = new Date();
        this.data = d.toLocaleString();
        this.permissao = "-rx-r--r--";
        atual = GerenciamentoEspaco.getInstance().getPosicaoLivre();

        if (nome.equals("/")) {

            this.posicaoPai = 0;
        } else {

            this.posicaoPai = pai;
        }

        // int atual = GerenciamentoEspaco.getInstance().getPosicaoLivre();
        HardDisk.getInstance().escreveBloco(gerarBinario(), atual);
        criaPonteiroFilho();

    }

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getPermissao() {
        return permissao;
    }

    public int getPai() {
        return pai;
    }

    public int getFilhos() {
        return filhos;
    }

    public void abstrair(String binario) {

        for (int i = 2; i < 184; i = i + 8) {
            this.setNome(this.nome + String.valueOf((char) Integer.parseInt(binario.substring(i, i + 8), 2)));
        }
        this.nome = this.nome.replaceAll(String.valueOf((char) (0000000000000000)), "");
        this.setData(this.data + String.valueOf(Integer.parseInt(binario.substring(186, +191), 2)) + "/" + String.valueOf(Integer.parseInt(binario.substring(191, 195), 2)) + "/"
                + String.valueOf(Integer.parseInt(binario.substring(195, 198), 2) + 2017) + " " + String.valueOf(Integer.parseInt(binario.substring(198, 203), 2)) + ":"
                + String.valueOf(Integer.parseInt(binario.substring(203, 209), 2)) + ":" + String.valueOf(Integer.parseInt(binario.substring(209, 215), 2)));

        permissao(binario.substring(215, 224));

        this.setPai(Integer.parseInt(binario.substring(224, 240), 2));
        this.setFilhos(Integer.parseInt(binario.substring(240, 256), 2));

        //imprimir();
    }

    public void imprimir() {

        }

    private void permissao(String permission) {
        if (permission.charAt(0) == '1') {
            this.setPermissao(this.permissao + "r");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(1) == '1') {
            this.setPermissao(this.permissao + "x");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(2) == '1') {
            this.setPermissao(this.permissao + "w");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(3) == '1') {
            this.setPermissao(this.permissao + "r");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(4) == '1') {
            this.setPermissao(this.permissao + "x");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(5) == '1') {
            this.setPermissao(this.permissao + "w");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(6) == '1') {
            this.setPermissao(this.permissao + "r");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(7) == '1') {
            this.setPermissao(this.permissao + "x");
        } else {
            this.setPermissao(this.permissao + "-");
        }
        if (permission.charAt(8) == '1') {
            this.setPermissao(this.permissao + "w");
        } else {
            this.setPermissao(this.permissao + "-");
        }

    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @param permissao the permissao to set
     */
    public void setPermissao(String permissao) {
        this.permissao = permissao;

    }

    public void atualizaPermissao(int posicao) {

        String auxiliarPermissao = "";

        for (int i = 1; i < 10; i++) {
            if (this.permissao.charAt(i) == '-') {
                auxiliarPermissao += "0";
            } else {
                auxiliarPermissao += "1";
            }
        }

        //215 - 224
        int inicio = 65536 + (posicao * 256) + 215;

        for (int i = inicio, k = 0; i < inicio + 9; i++, k++) {
            if (auxiliarPermissao.charAt(k) == '1') {
                HardDisk.getInstance().setBitDaPosicao(true, i);
            } else {
                HardDisk.getInstance().setBitDaPosicao(false, i);
            }
        }

    }

    /**
     * @param pai the pai to set
     */
    public void setPai(int pai) {
        this.pai = pai;
    }

    /**
     * @param filhos the filhos to set
     */
    public void setFilhos(int filhos) {
        this.filhos = filhos;
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

    public void addFilhosBinario(int filho) {
        Ponteiros p = new Ponteiros(posicaoFilhos);
        p.abstrair(HardDisk.getInstance().returnBloco(posicaoFilhos));
        p.adicionaFilho(filho);

    }

    private void criaPonteiroFilho() {
        Ponteiros p = new Ponteiros(posicaoFilhos);

        p.setPai(getPosicaoPai());

    
        HardDisk.getInstance().escreveBloco(p.gerarBinario(), getPosicaoFilhos());

    }

    private String completaBinario(String s, int tamanho) {

        while (s.length() < tamanho) {
            s = "0" + s;
        }

        return s;

    }

    public int getPosicaoFilhos() {
        return posicaoFilhos;
    }

    public void setPosicaoFilhos(int posicaoFilhos) {
        this.posicaoFilhos = posicaoFilhos;
    }

    public int getPosicaoPai() {
        return posicaoPai;
    }

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
