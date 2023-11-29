package com.example;

public class PDFDeEstudo {
    private String arquivo;
    private int numeroDePaginas;

    // Construtor
    public PDFDeEstudo(String arquivo, int numeroDePaginas) {
        this.arquivo = arquivo;
        this.numeroDePaginas = numeroDePaginas;
    }

     public PDFDeEstudo() {
    }


    // Getter para o arquivo
    public String getArquivo() {
        return arquivo;
    }

    //public String getNomeDoArquivo() {
    //    int ultimoIndice = arquivo.lastIndexOf("\\");        
    //    String nomeDoArquivo = ultimoIndice >= 0 ? arquivo.substring(ultimoIndice + 1) : arquivo;
    //    return nomeDoArquivo;
   // }

    // Setter para o arquivo
    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    // Getter para o número de páginas
    public int getNumeroDePaginas() {
        return numeroDePaginas;
    }

    // Setter para o número de páginas
    public void setNumeroDePaginas(int numeroDePaginas) {
        this.numeroDePaginas = numeroDePaginas;
    }

    @Override
    public String toString (){
        return this.getArquivo() + " " + this.getNumeroDePaginas();
    }
}