package Abstracoes;

import hardware.HardDisk;

public class Conteudo {

    private String conteudo;
    private int proximo;
    private boolean continua;

    public Conteudo() {
        continua = false;

    }

    public String getConteudo() {
        return conteudo;
    }

    public int gerarBinario(String conteudo) {
        int posicao = GerenciamentoEspaco.getInstance().getPosicaoLivre();
        StringBuilder binario = new StringBuilder();
        binario.append("11");
        String frase = "";
        if (conteudo.length() < 29) {

            for (int i = 0; i < conteudo.length(); i++) {
                frase += Integer.toBinaryString(0x100 | (int) conteudo.charAt(i)).substring(1);

            }

            binario.append(completaBinario(frase, 232));

            binario.append("0");
            binario.append("0000000000000");

            while (binario.toString().length() < 256) {
                binario.append("0");
            }

            HardDisk.getInstance().escreveBloco(binario.toString(), posicao);
        } else {
            for (int i = 0; i < 29; i++) {
                frase += Integer.toBinaryString(0x100 | (int) conteudo.charAt(i)).substring(1);
            }

            binario.append(frase);
            binario.append("1");

            int proximoConteudo = gerarBinario(conteudo.substring(29));
            binario.append(completaBinario(Integer.toBinaryString(proximoConteudo), 16));
            binario.append("00000");
            HardDisk.getInstance().escreveBloco(binario.toString(), posicao);

        }
        return posicao;
    }

    public String abstrair(String binario) {
        String resposta = "";

        for (int i = 2; i < 232; i = i + 8) {
            resposta = resposta + String.valueOf((char) Integer.parseInt(binario.substring(i, i + 8), 2));
        }

        if (binario.charAt(234) == '1') {
            int proximoC = Integer.parseInt(binario.substring(235, 251), 2);
            Conteudo novo = new Conteudo();
            resposta = resposta + novo.abstrair(HardDisk.getInstance().returnBloco(proximoC));

        }

        return resposta.replaceAll(String.valueOf((char) (0000000000000000)), "");

    }

    private String retornaString(String binario) {
        String texto = "";
        for (int i = 2; i < 234; i = i + 8) {
            texto = texto + String.valueOf((char) Integer.parseInt(binario.substring(i, i + 8), 2));
        }

        if (binario.charAt(234) == '1') {
            int posicao = Integer.parseInt(binario.substring(235, 251), 2);

            texto = texto + retornaString(HardDisk.getInstance().returnBloco(posicao));
        }

        return texto;

    }

    private String completaBinario(String s, int tamanho) {

        while (s.length() < tamanho) {
            s = "0" + s;
        }

        return s;

    }

}
