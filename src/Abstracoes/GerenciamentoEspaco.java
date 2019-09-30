package Abstracoes;

import hardware.HardDisk;

public class GerenciamentoEspaco {
    
    private static GerenciamentoEspaco ge;

    
    private GerenciamentoEspaco() {
 
    }
    
    public static synchronized GerenciamentoEspaco getInstance() {
        if (ge == null) {
            ge = new GerenciamentoEspaco();
        }
        
        return ge;
        
    }
    
    public int getPosicaoLivre() {
        for (int i = 0; i < 65536; i++) {
            if (!HardDisk.getInstance().getBitDaPosicao(i)) {
                HardDisk.getInstance().setBitDaPosicao(true, i);
                return i;
            }
        }
        return -1;
    }
    
    public void clearPosicao(int i) {
        HardDisk.getInstance().setBitDaPosicao(false, i);
    }
    
    
    
    
    
}
