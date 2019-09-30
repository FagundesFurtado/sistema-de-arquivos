package Abstracoes;

import hardware.HardDisk;
import java.util.Date;

public class Arquivo {

    private String nome;
    private String data;
    private String permissao = "-";
    private int pai;
    private int conteudo;
    private String conteudo2;
    private int atual;

    public int getAtual() {
        return atual;
    }

    public void setAtual(int atual) {
        this.atual = atual;
    }

    public Arquivo() {
    }

    public Arquivo(String nome, String conteudo, int pai) {
        this.nome = nome;
        Date d = new Date();
        this.data = d.toLocaleString();
        this.permissao = "-rx-r--r--";
        this.conteudo2 = conteudo;
        this.pai = pai;

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

    public int getConteudo() {
        return conteudo;
    }

    public void abstrair(String binario) {

        for (int i = 2; i < 184; i = i + 8) {
            this.nome += String.valueOf((char) Integer.parseInt(binario.substring(i, i + 8), 2));
        }
        this.nome = this.nome.replaceAll(String.valueOf((char) (0000000000000000)), "").replace("null", "");

         this.data += String.valueOf(Integer.parseInt(binario.substring(186, +191), 2)) + "/" + String.valueOf(Integer.parseInt(binario.substring(191, 195), 2)) + "/"
                + String.valueOf(Integer.parseInt(binario.substring(195, 198), 2) + 2017) + " " + String.valueOf(Integer.parseInt(binario.substring(198, 203), 2)) + ":"
                + String.valueOf(Integer.parseInt(binario.substring(203, 209), 2)) + ":" + String.valueOf(Integer.parseInt(binario.substring(209, 215), 2));

        permissao(binario.substring(215, 224));

        this.pai = Integer.parseInt(binario.substring(224, 240), 2);
        this.conteudo = Integer.parseInt(binario.substring(240, 256), 2);

        imprimir();

    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;

    }

    public void atualiza(int posicao) {
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

    public void imprimir() {

      }

    public String gerarBinario() {
        StringBuilder abstracao = new StringBuilder();

        abstracao.append("01");

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

        String paiBinario = Integer.toBinaryString(pai);
        while (paiBinario.length() < 16) {
            paiBinario = "0" + paiBinario;
        }
        abstracao.append(paiBinario);

        Conteudo c = new Conteudo();
        int posicaoConteudo = c.gerarBinario(conteudo2);

        String conteudoBinario = Integer.toBinaryString(posicaoConteudo);

        while (conteudoBinario.length() < 16) {
            conteudoBinario = "0" + conteudoBinario;
        }
        abstracao.append(conteudoBinario);

        return abstracao.toString();
    }

    private void permissao(String permission) {
        if (permission.charAt(0) == '1') {
            this.permissao += "r";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(1) == '1') {
            this.permissao += "x";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(2) == '1') {
            this.permissao += "w";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(3) == '1') {
            this.permissao += "r";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(4) == '1') {
            this.permissao += "x";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(5) == '1') {
            this.permissao += "w";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(6) == '1') {
            this.permissao += "r";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(7) == '1') {
            this.permissao += "x";
        } else {
            this.permissao += "-";
        }
        if (permission.charAt(8) == '1') {
            this.permissao += "w";
        } else {
            this.permissao += "-";
        }

    }

    private String completaBinario(String s, int tamanho) {

        while (s.length() < tamanho) {
            s = "0" + s;
        }

        return s;

    }

}
