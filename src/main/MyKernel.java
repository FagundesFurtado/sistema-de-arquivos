package main;

import Abstracoes.Arquivo;
import Abstracoes.Conteudo;
import Abstracoes.DiretórioAtual;
import Abstracoes.GerenciamentoEspaco;
import Abstracoes.Ponteiros;
import binary.Binario;
import controller.Arquivos;
import controller.Diretorio;
import hardware.HardDisk;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import operatingSystem.Kernel;

public class MyKernel implements Kernel {

    //public Diretorio raiz;
    // public Diretorio atual;
    public Binario binario;
    public int atualHD;

    public DiretórioAtual da;

    public MyKernel() {

        atualHD = 0;
        DiretórioAtual raiz = new DiretórioAtual("/", 0);

        //raiz = new Diretorio(raiz, "/");
        //atual = raiz;
        binario = new Binario();

        //da = new DiretórioAtual();
        //da.abstrair("0001100010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001101010100000111101111101011111010010000000000000000000000000000000101\n" +
//"");
        escritaHD();

    }

    private void leituraHD() {

    }

    private void escritaHD() {

    }

    public String ls(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: ls");
        System.out.println("\tParametros: " + parameters);
        //Simple Date Format

        switch (parameters) {
            case "":
                DiretórioAtual novo = new DiretórioAtual();
                novo.abstrair(HardDisk.getInstance().returnBloco(atualHD));

                Ponteiros p = new Ponteiros(novo.getFilhos());
                ArrayList<String> filhotes = p.abstrair(HardDisk.getInstance().returnBloco(novo.getFilhos()));

                for (String s : filhotes) {

                    result += s;
                    result += "\n";
                }
                break;
            case "-l":

                result = funcaoLs(atualHD, true);
                break;
            default:
                result = funcaoLs(retornaDiretorioBinario(atualHD, parameters.replace("-l ", "")), true);
                break;

        }
        return result;
    }

    public String mkdir(String parameters) {
        String result = "";
        if (!parameters.equals("")) {
            if (parameters.charAt(0) == '/') {
                parameters = parameters.replaceFirst("/", "~/");
            }
            String aux[] = parameters.split("/");
            int diretorioAuxiliar = atualHD;

            String nome = parameters;
            if (aux.length > 1) {
                String caminho = "";
                for (int i = 0; i < aux.length - 1; i++) {
                    caminho += aux[i] + "/";
                }
                nome = aux[aux.length - 1];
                diretorioAuxiliar = retornaDiretorioBinario(diretorioAuxiliar, caminho);
            }

            boolean existe = false;
            DiretórioAtual da = new DiretórioAtual();
            da.abstrair(HardDisk.getInstance().returnBloco(diretorioAuxiliar));

            Ponteiros p = new Ponteiros(da.getFilhos());
            ArrayList<String> filhoss = p.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));
            for (String s : filhoss) {
                System.out.println(s);
            }

            for (int i = 0; i < filhoss.size(); i++) {
                String split[] = filhoss.get(i).split("-");
                if (split[0].equals(nome)) {
                    existe = true;
                    result = "Diretório já existente";
                }
            }
            if (!existe) {

                DiretórioAtual novoFilho = new DiretórioAtual(nome, diretorioAuxiliar);

                int posicaoAdicionar = novoFilho.getAtual();
                //HardDisk.getInstance().escreveBloco(novoFilho.gerarBinario(), posicaoAdicionar);

                DiretórioAtual dad = new DiretórioAtual();
                dad.abstrair(HardDisk.getInstance().returnBloco(diretorioAuxiliar));
                Ponteiros novo = new Ponteiros(dad.getFilhos());
                result = novo.adicionaFilho(posicaoAdicionar);

            }
        }
        return result;
    }

    public String cd(String parameters) {

        String result = "";
        String currentDir = "";
//        Diretorio aux = atual;
        DiretórioAtual novo;
        int posicaoAux = atualHD;
        System.out.println(parameters);
        switch (parameters) {
            case "..":
                novo = new DiretórioAtual();
                novo.abstrair(HardDisk.getInstance().returnBloco(atualHD));
                posicaoAux = novo.getPai();
                break;
            case "/":
                posicaoAux = 0;
                break;
            case "":
                result = "Caminho inválido";
                break;
            default:
                posicaoAux = retornaDiretorioBinario(posicaoAux, parameters);
                break;
        }

        System.out.println("Posicao antes " + atualHD);
        System.out.println("Nova posicao " + posicaoAux);

        if (posicaoAux == atualHD && !parameters.equals("/")) {
            result = "Diretório não existe";
        }

        System.out.println("POSICAO AUX " + posicaoAux);
        System.out.println("AUTAL " + atualHD);

        atualHD = posicaoAux;

        currentDir = currentDir(atualHD) + "/";
        System.out.println("Diretorio: " + currentDir);
        operatingSystem.fileSystem.FileSytemSimulator.currentDir = currentDir;
        return result;
    }

    public String rmdir(String parameters) {
        String result = "";
        System.out.println("Chamada de Sistema: rmdir");
        System.out.println("\tParametros: " + parameters);

        parameters = parameters.replace("-r ", "");
        System.out.println("\tParametros: " + parameters);
        if (parameters.charAt(0) == '/') {
            parameters = parameters.replaceFirst("/", "~/");
        }

        String diretorios[] = parameters.split("/");

        int diretorioAuxiliar = retornaDiretorioBinario(atualHD, parameters);
        DiretórioAtual da = new DiretórioAtual();
        da.abstrair(HardDisk.getInstance().returnBloco(diretorioAuxiliar));

        System.out.println("Nome pai " + da.getNome());
        String nomeProcurado = diretorios[diretorios.length - 1].replaceAll(" ", "");
        System.out.println("Nome procurando " + nomeProcurado);

        Ponteiros filhos = new Ponteiros(da.getFilhos());

        ArrayList<String> vetorFilhos = filhos.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));

        if (vetorFilhos.size() == 1) {
            int a = filhos.getPai();
            DiretórioAtual seila = new DiretórioAtual();
            seila.abstrair(HardDisk.getInstance().returnBloco(a));
            DiretórioAtual dad = new DiretórioAtual();
            dad.abstrair(HardDisk.getInstance().returnBloco(seila.getPai()));

            Ponteiros f = new Ponteiros(dad.getFilhos());
            ArrayList<String> filhosF = f.abstrair(HardDisk.getInstance().returnBloco(dad.getFilhos()));

            boolean excluiu = false;
            for (int i = 1; i < filhosF.size(); i++) {
                String[] split = filhosF.get(i).split("-");
                System.out.println("Nomes " + split[0]);
                System.out.println("Comparando com " + diretorios[diretorios.length - 1]);

                if (split[0].equals(nomeProcurado)) {
                    HardDisk.getInstance().setBitDaPosicao(false, Integer.parseInt(split[1]));
                    System.out.println("Posicao " + split[2]);
                    f.setPosicaoUsados(false, Integer.parseInt(split[2]));
                    excluiu = true;
                    i = filhosF.size();
                } else {
                    result = "Diretório nao existe";
                }

            }
            if (excluiu) {
                result = "";
            }
        } else {
            //  System.out.println("Removeu "+nomeProcurado);
            result = "Diretório possui arquivos e/ou diretorios";
        }

        return result;
    }

    public String cp(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: cp");
        System.out.println("\tParametros: " + parameters);
        String aux[] = parameters.split(" ");

        //funcaoCp(aux[0], aux[1], result);
        return result;
    }

    public String mv(String parameters) {

        String result = "";
        System.out.println("Chamada de Sistema: mv");
        System.out.println("\tParametros: " + parameters);

        String aux[] = parameters.split(" ");

        // funcaoMV(aux[0], aux[1], result);
        return result;
    }

    private void file2file(Diretorio origem, Diretorio destino, String nomeOrigem, String nomeDestino) {
        if (origem == destino) {
            for (Arquivos a : origem.getArquivos()) {
                if (a.getNome().equals(nomeOrigem)) {
                    a.setNome(nomeDestino);
                }
            }
        } else {
            System.out.println("Erro: Nao existe este arquivo para mover");
        }
    }

    private void file2folder(Diretorio origem, Diretorio destino, String nomeOrigem, String nomeDestino) {
        for (int i = 0; i < origem.getArquivos().size(); i++) {
            if (origem.getArquivos().get(i).getNome().equals(nomeOrigem)) {

                destino.addArquivos(origem.getArquivos().remove(i));

                i = origem.getArquivos().size();
            }
        }
    }

    private void folder2folder(Diretorio origem, Diretorio destino, String nomeOrigem, String nomeDestino) {
        Diretorio aux = origem.getPai();

        for (int i = 0; i < aux.getFilhos().size(); i++) {
            if (aux.getFilhos().get(i).getNome().equals(nomeOrigem)) {

                destino.getFilhos().add(aux.getFilhos().remove(i));
                i = aux.getFilhos().size();

                System.out.println("Destino " + destino.getNome());
                System.out.println("Origem " + aux.getNome());
                System.out.println("Arquivo para mover " + origem.getNome());

            }
        }

    }

    private void copiar(Diretorio original, Diretorio copiar) {

        for (int i = 0; i < original.getArquivos().size(); i++) {
//            copiar.addArquivos(original.);

        }

    }

    public String rm(String parameters) {

        String result = "";
        System.out.println("Chamada de Sistema: rm");
        System.out.println("\tParametros: " + parameters);

        String aux = parameters.replace("-r", "");
        String aux2[] = aux.split("/");

        //  int posicao = retornaDiretorioBinario(atualHD, aux);
        rmdir(aux);
        if (aux2.length == 1) {

            rmdir(parameters.replace("-r", ""));

//            if (parameters.charAt(0) == '/') {
//                parameters = parameters.replaceFirst("/", "~/");
//            }
//            if (parameters.contains(".txt")) {
//                String aux2[] = parameters.split("/");
//                String caminho = "";
//                for (int i = 0; i < aux2.length - 1; i++) {
//                    caminho += aux2[i] + "/";
//                }
//                int diretorio = retornaDiretorioBinario(atualHD, caminho);
//
//                DiretórioAtual da = new DiretórioAtual();
//                da.abstrair(hardware.HardDisk.getInstance().returnBloco(diretorio));
//
//                Ponteiros pa = new Ponteiros(da.getFilhos());
//                ArrayList<String> filhos = pa.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));
//
//                for (int i = 0; i < filhos.size(); i++) {
//
//                    String split[] = filhos.get(i).split("-");
//
//                    if (split[1].equals(aux2[aux2.length - 1])) {
//                        HardDisk.getInstance().setBitDaPosicao(false, Integer.parseInt(split[1]));
//                        pa.setPosicaoUsados(false, Integer.parseInt(split[2]));
//                        i = filhos.size();
//                    }
//                }
        }
//        else {
//            System.out.println("Nome pasta PARA REMOVER" + aux2[aux2.length - 1]);
//            rmdir(parameters.replaceAll("-r", "").replaceAll(" ", ""));
//            if (aux[1].charAt(0) == '/') {
//               aux[1] = aux[1].replaceFirst("/", "~/");
//            }
//            int diretorioRemover = retornaDiretorioBinario(atualHD, aux[1]);
//
//            DiretórioAtual remover = new DiretórioAtual();
//            remover.abstrair(HardDisk.getInstance().returnBloco(diretorioRemover));
//
//            DiretórioAtual paiRemover = new DiretórioAtual();
//            paiRemover.abstrair(HardDisk.getInstance().returnBloco(remover.getPai()));
//
//            Ponteiros p = new Ponteiros(paiRemover.getFilhos());
//            ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(paiRemover.getFilhos()));
//
//            for (int i = 0; i < filhos.size(); i++) {
//                String split[] = filhos.get(i).split("-");
//                if (split[1].equals(aux2[aux2.length - 1])) {
//                    p.setPosicaoUsados(false, Integer.parseInt(split[2]));
//                    HardDisk.getInstance().setBitDaPosicao(false, Integer.parseInt(split[1]));
//                    funcaoRemoverFilhos(Integer.parseInt(split[1]));
//                    i = filhos.size();
//                }
//            }
//
//        }

        return result;
    }

    private void funcaoRemoverFilhos(int remover) {

        DiretórioAtual d = new DiretórioAtual();
        d.abstrair(HardDisk.getInstance().returnBloco(remover));
        HardDisk.getInstance().setBitDaPosicao(false, remover);

        Ponteiros p = new Ponteiros(d.getFilhos());

        ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(d.getFilhos()));

        for (int i = 0; i < filhos.size(); i++) {
            String split[] = filhos.get(i).split("-");
            funcaoRemoverFilhos(Integer.parseInt(split[1]));
        }
        HardDisk.getInstance().setBitDaPosicao(false, d.getFilhos());

    }

    public String chmod(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: chmod");
        System.out.println("\tParametros: " + parameters);
        String aux[] = parameters.split(" ");
        String permission;
        if (aux.length == 2) {
            permission = aux[0];
        } else {
            permission = aux[1];
        }

        StringBuilder permissao = new StringBuilder("-");
//rxw
        for (int i = 0; i < 3; i++) {
            switch (permission.charAt(i)) {
                case '0':
                    permissao.append("---");
                    break;
                case '1':
                    permissao.append("--w");
                    break;
                case '2':
                    permissao.append("-x-");
                    break;
                case '3':
                    permissao.append("-xw");
                    break;
                case '4':
                    permissao.append("r--");
                    break;
                case '5':
                    permissao.append("r-w");
                    break;
                case '6':
                    permissao.append("rx-");
                    break;
                case '7':
                    permissao.append("rxw");
                    break;
                default:
                    System.out.println("Erro de permissao");
                    result = "Erro de permissao";
                    break;
            }
        }
        System.out.println(permissao);
        //Ver documentacao do CHMOD
        if (aux.length == 2) {

            String caminho[] = aux[1].split("/");
            String path = "";
            for (int i = 0; i < caminho.length - 1; i++) {
                path = "/" + caminho[i];
            }
            int diretorioAux = retornaDiretorioBinario(atualHD, path);

            DiretórioAtual da = new DiretórioAtual();
            da.abstrair(HardDisk.getInstance().returnBloco(diretorioAux));
            Ponteiros p = new Ponteiros(da.getFilhos());
            ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));

            for (int i = 0; i < filhos.size(); i++) {
                String split[] = filhos.get(i).split("-");
                if (split[0].equals(caminho[caminho.length - 1])) {
                    if (split[0].contains(".txt")) {
                        Arquivo arq = new Arquivo();
                        arq.abstrair(HardDisk.getInstance().returnBloco(Integer.parseInt(split[1])));
                        arq.setAtual(Integer.parseInt(split[1]));
                        arq.setPermissao(permissao.toString());
                        arq.atualiza(Integer.parseInt(split[1]));

                    } else {
                        DiretórioAtual da2 = new DiretórioAtual();
                        da2.abstrair(HardDisk.getInstance().returnBloco(Integer.parseInt(split[1])));
                        System.out.println("Nome diretorio " + da2.getNome());
                        da2.setPermissao(permissao.toString());
                        da2.atualizaPermissao(Integer.parseInt(split[1]));

                    }

                }

            }

//            Diretorio d = retornaDiretorio(atual, path);
//            for (Arquivos a : d.getArquivos()) {
//                if (a.getNome().equals(caminho[caminho.length - 1])) {
//                    a.setPermissao(permissao.toString());
//                }
//            }
        }
//        else {
//            setPermissionRecursive(permissao.toString(), retornaDiretorio(atual, aux[2]));
//        }

        return result;
    }

    private void setPermissionRecursive(String permissao, Diretorio d) {
        d.setPermissao(permissao);
        for (Arquivos a : d.getArquivos()) {
            a.setPermissao(permissao);
        }
        for (Diretorio dir : d.getFilhos()) {
            setPermissionRecursive(permissao, dir);
        }
    }

    public String createfile(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: createfile");
        System.out.println("\tParametros: " + parameters);

        String aux[] = parameters.split(" ");

        int diretorio = retornaDiretorioBinario(atualHD, aux[0]);

        if (aux[0].charAt(0) == '/') {
            aux[0] = aux[0].replaceFirst("/", "~/");
        }

        String nome[] = aux[0].split("/");
        String nomeArquivo = nome[nome.length - 1];

        DiretórioAtual da = new DiretórioAtual();
        da.abstrair(HardDisk.getInstance().returnBloco(diretorio));

        Ponteiros p = new Ponteiros(da.getFilhos());
        ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));

        if (verificaSeExisteArquivo(filhos, nomeArquivo)) {

            StringBuilder conteudo = new StringBuilder();
            for (int i = 1; i < aux.length; i++) {
                conteudo.append(aux[i]);
                conteudo.append(" ");
            }

            String conteudoFinal = "";

            for (int i = 0; i < conteudo.length(); i++) {
                if (conteudo.charAt(i) != '\\') {
                    conteudoFinal += conteudo.charAt(i);
                } else {
                    if (conteudo.charAt(i + 1) == 'n') {
                        conteudoFinal += "\n";
                        i++;
                    } else {
                        conteudoFinal += "\\";
                    }

                }

            }
            Arquivo arq = new Arquivo(nomeArquivo, conteudoFinal, da.getAtual());
            int posicaoArquivo = GerenciamentoEspaco.getInstance().getPosicaoLivre();
            HardDisk.getInstance().escreveBloco(arq.gerarBinario(), posicaoArquivo);
            p.adicionaFilho(posicaoArquivo);

        } else {
            result = "Arquivo já existe.";
        }

        return result;
    }

    private boolean verificaSeExisteArquivo(ArrayList<String> filhos, String nome) {
        boolean resp = true;

        for (int i = 0; i < filhos.size(); i++) {
            String split[] = filhos.get(i).split("-");
            if (split[0].equals(nome)) {
                resp = false;
                i = filhos.size();
            }
        }
        return resp;
    }

    public String cat(String parameters) {
        String result = "";
        System.out.println("Chamada de Sistema: cat");
        System.out.println("\tParametros: " + parameters);
        if (parameters.charAt(0) == '/') {

            parameters = parameters.replaceFirst("/", "~/");
        }

        String aux[] = parameters.split("/");

        StringBuilder caminho = new StringBuilder();

        for (int i = 0; i < aux.length - 1; i++) {
            caminho.append(aux[i]);
            caminho.append("/");
        }

        System.out.println("Caminho " + caminho.toString());

        int diretorio = retornaDiretorioBinario(atualHD, caminho.toString());

        DiretórioAtual da = new DiretórioAtual();
        da.abstrair(HardDisk.getInstance().returnBloco(diretorio));
        Ponteiros p = new Ponteiros(da.getFilhos());
        ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));

        for (int i = 0; i < filhos.size(); i++) {
            String split[] = filhos.get(i).split("-");
            if (split[0].equals(aux[aux.length - 1])) {
                Arquivo arq = new Arquivo();
                arq.abstrair(HardDisk.getInstance().returnBloco(Integer.parseInt(split[1])));
                Conteudo c = new Conteudo();
                result = c.abstrair(HardDisk.getInstance().returnBloco(arq.getConteudo()));

                i = filhos.size();
            }

        }

        return result;
    }

    public String batch(String parameters) {
        String result = "";
        System.out.println("Chamada de Sistema: batch");
        System.out.println("\tParametros: " + parameters);
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(parameters));
            while (true) {
                String linha = buffRead.readLine();
                if (linha != null) {

                    String aux[] = linha.split(" ");

                    switch (aux[0]) {

                        case "cd":
                            System.out.println("CD " + linha.replace("cd ", ""));
                            cd(linha.replace("cd ", ""));
                            break;
                        case "mkdir":
                            mkdir(linha.replace("mkdir ", ""));
                            break;
                        case "ls":
                            ls(linha.replace("ls ", ""));
                            break;
                        case "createfile":
                            createfile(linha.replace("createfile ", ""));
                            break;
                        case "chmod":
                            chmod(linha.replace("chmod", ""));
                            break;
                        case "rm":
                            rm(linha.replace("rm", ""));
                            break;
                        case "dump":
                            dump(linha.replace("dump", result));
                            break;
                    }

                } else {
                    break;

                }

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyKernel.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(MyKernel.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    private void funcaoDump(int d, StringBuilder saida) {

        DiretórioAtual da = new DiretórioAtual();
        da.abstrair(HardDisk.getInstance().returnBloco(d));
        System.out.println("To na " + da.getNome());
        Ponteiros p = new Ponteiros(da.getPosicaoFilhos());

        System.out.println("Posicao filhos " + da.getPosicaoFilhos());
        ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(da.getPosicaoFilhos()));

        for (int i = 1; i < filhos.size(); i++) {
            System.out.println("FIlhos");
            String split[] = filhos.get(i).split("-");
            if (split[0].contains(".txt")) {
                saida.append("createfile ");
                saida.append("./");
                saida.append(split[0]);
                saida.append(" ");
                Arquivo arq = new Arquivo();
                arq.abstrair(HardDisk.getInstance().returnBloco(Integer.parseInt(split[1])));
                Conteudo c = new Conteudo();
                saida.append(c.abstrair(HardDisk.getInstance().returnBloco(arq.getConteudo())));

            } else {
                saida.append("mkdir ");
                saida.append(split[0]);
                saida.append("\n");

                funcaoDump(Integer.parseInt(split[1]), saida);

            }

        }

    }
//C:\Users\Cris-\Desktop\saida.txt

    public String dump(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: dump");
        System.out.println("\tParametros: " + parameters);

        StringBuilder saida = new StringBuilder();
        funcaoDump(0, saida);
        System.out.println(saida.toString());
        File file = new File(parameters);
        if (file.exists()) {
            BufferedWriter bw = null;
            try {
                //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
                System.out.println("Arquivo existente");
                bw = new BufferedWriter(new FileWriter(file, false));
                bw.write(saida.toString());
                bw.flush();
                bw.close();
                System.out.println(saida.toString());

            } catch (IOException ex) {
                Logger.getLogger(MyKernel.class
                        .getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    bw.close();

                } catch (IOException ex) {
                    Logger.getLogger(MyKernel.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = "Arquivo não existe.";
        }

        return result;
    }

    public String info() {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: info");
        System.out.println("\tParametros: sem parametros");

        //nome do aluno
        String name = "Fera da Silva";
        //numero de matricula
        String registration = "2001.xx.yy.00.11";
        //versao do sistema de arquivos
        String version = "0.1";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

    private String funcaoLs(int local, boolean l) {
        StringBuilder arquivos = new StringBuilder();

        DiretórioAtual da = new DiretórioAtual();
        da.abstrair(HardDisk.getInstance().returnBloco(local));

        Ponteiros p = new Ponteiros(da.getFilhos());
        ArrayList<String> filhos = p.abstrair(HardDisk.getInstance().returnBloco(da.getFilhos()));

        for (int i = 0; i < filhos.size(); i++) {
            String split[] = filhos.get(i).split("-");
            if (split[0].contains(".txt")) {
                arquivos.append(split[0]);
                Arquivo arq = new Arquivo();
                arq.abstrair(HardDisk.getInstance().returnBloco(Integer.parseInt(split[1])));
                arquivos.append(" ");
                arquivos.append(arq.getPermissao());
                arquivos.append(" ");
                arquivos.append(arq.getData());
                arquivos.append("\n");

            } else {
                arquivos.append(split[0]);
                DiretórioAtual da2 = new DiretórioAtual();
                da2.abstrair(HardDisk.getInstance().returnBloco(Integer.parseInt(split[1])));
                arquivos.append(" ");
                arquivos.append(da2.getPermissao());
                arquivos.append(" ");
                arquivos.append(da2.getData());
                arquivos.append("\n");
            }

        }

        return arquivos.toString();
    }

    private int funcaoCdBinario(int d, String nome) {

        int existe = -1;
        DiretórioAtual novo = new DiretórioAtual();
        novo.abstrair(HardDisk.getInstance().returnBloco(d));

        int filhos = novo.getFilhos();

        Ponteiros p = new Ponteiros(filhos);

        ArrayList<String> CDfilhos = p.abstrair(HardDisk.getInstance().returnBloco(filhos));

        for (int i = 0; i < CDfilhos.size(); i++) {
            String split[] = CDfilhos.get(i).split("-");
            String nomeTeste = split[0].replaceAll(String.valueOf((char) (0000000000000000)), "");

            if (nomeTeste.equals(nome)) {

                existe = Integer.parseInt(split[1]);
                i = CDfilhos.size();
            }
        }
        return existe;
    }

    private int funcaoCd(Diretorio d, String nome) {

        int existe = -1;
        for (int i = 0; i < d.getFilhos().size(); i++) {
            if (d.getFilhos().get(i).getNome().equals(nome)) {
                existe = i;
                i = d.getFilhos().size();
            }
        }
        return existe;
    }

    private String currentDir(int d) {

        if (d != 0) {

            DiretórioAtual da = new DiretórioAtual();
            da.abstrair(HardDisk.getInstance().returnBloco(d));
            return (currentDir(da.getPai()) + "/" + da.getNome());
        }
        return "";
    }

    private int retornaDiretorioBinario(int d, String caminho) {
        int origem = d;
        if (caminho.charAt(0) == '/') {
            caminho = caminho.replaceFirst("/", "~/");
            System.out.println(caminho);
        }
        String aux[] = caminho.split("/");
        DiretórioAtual novo;
        for (int i = 0; i < aux.length; i++) {

            switch (aux[i]) {
                case "..":
                    novo = new DiretórioAtual();
                    novo.abstrair(HardDisk.getInstance().returnBloco(d));
                    d = novo.getPai();
                    break;
                case ".":
                    d = d;
                    break;
                case "~":
                    d = 0;
                    break;
                default:

                    if (funcaoCdBinario(d, aux[i]) >= 0) {
                        d = funcaoCdBinario(d, aux[i]);
                        //  d = d.getFilhos().get(funcaoCdBinario(d, aux[i]));

                    }
                    break;
            }
        }
        origem = d;
        return origem;
    }

}
