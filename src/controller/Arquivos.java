package controller;

import java.util.Date;

public class Arquivos implements Cloneable {

    private String nome;
    private String arquivo;
    private String modificacao;
    private String permissao;

    public Arquivos(String nome, String arquivo) {
        this.nome = nome;
        this.arquivo = arquivo;
        Date d = new Date();
        this.modificacao = d.toLocaleString();
        this.permissao = "-rx-r--r--";
    }

    public Arquivos(String nome, String arquivo, String permissao, String data) {
        this.nome = nome;
        this.arquivo = arquivo;
        this.modificacao = data;
        this.permissao = permissao;
    }

    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getArquivo() {
        return arquivo;
    }


    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }


    public String getModificacao() {
        return modificacao;
    }


    public void setModificacao(String modificacao) {
        this.modificacao = modificacao;
    }


    public String getPermissao() {
        return permissao;
    }


    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    public Arquivos clone() throws CloneNotSupportedException {
        return (Arquivos) super.clone();
    }

}
