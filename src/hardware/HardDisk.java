package hardware;

public class HardDisk {

    private boolean hardDisk[];
    private int numeroDeBits;

    private static HardDisk hd;

    private HardDisk() {
        this.numeroDeBits = 16 * 8 * 1024 * 1024;
        this.hardDisk = new boolean[this.numeroDeBits];

    }

    public static synchronized HardDisk getInstance() {
        if (hd == null) {
            hd = new HardDisk();
        }

        return hd;
    }

    public void inicializarMemoriaSecundaria() {
        for (int i = 0; i < numeroDeBits; i++) {
            this.hardDisk[i] = false;
        }
    }

    public void setBitDaPosicao(boolean bit, int posicao) {
        this.hardDisk[posicao] = bit;
    }

    public boolean getBitDaPosicao(int i) {
        return hardDisk[i];
    }

    public String returnBloco(int i) {
        StringBuilder bloco = new StringBuilder();
        int valor = (i * 256) + 65536;
        for (int j = valor; j < valor + 256; j++) {
            if (hardDisk[j]) {
                bloco.append("1");
            } else {
                bloco.append("0");
            }

            // bloco.append(hardDisk[j]);
        }
        return bloco.toString();
    }

    public void escreveBloco(String bloco, int i) {

     
        int valor = 65536 + (i * 256);
        for (int j = valor, k = 0; j < valor + 256; j++, k++) {
            if (bloco.charAt(k) == '1') {
                setBitDaPosicao(true, j);
            } else {
                setBitDaPosicao(false, j);
            }

        }



    }

    public void escrevePonteiro(String ponteiro, int i) {
        for (int j = i, k=0; j < i + 16; j++, k++) {
            if (ponteiro.charAt(k) == '1') {
                setBitDaPosicao(true, j);
            } else {
                setBitDaPosicao(false, j);
            }

        }
    }

 

}
