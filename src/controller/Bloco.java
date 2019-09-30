package controller;

import hardware.HardDisk;

public class Bloco {
    
    private HardDisk hd;
    private int posicao;
    
    public Bloco(HardDisk hd) {
        this.hd = hd;
    }
    
    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }
    
    public String getNome() {
        
        String resultado = "";
        for (int i = posicao; i <=posicao + 20; i = i + 8) {
            String valor = "";
            for (int j = i; j < i + 8; j++) {
                if (hd.getBitDaPosicao(j)) {
                    valor += "1";
                } else {
                    valor += "0";
                }
            }
            resultado += String.valueOf((char) Integer.parseInt(valor, 2));
            
        }
        
        return resultado;
    }
    
}
