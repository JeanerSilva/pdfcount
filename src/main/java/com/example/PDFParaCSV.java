package com.example;

public class PDFParaCSV {
    private String dia;
    private String arquivo;
    private String paginas;

    // Construtor
    public PDFParaCSV(String dia, String arquivo, String paginas) {
        this.arquivo = arquivo;
        this.dia = dia;
        this.paginas = paginas;
    }

     public PDFParaCSV() {
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getPaginas() {
        return paginas;
    }

    public void setNumeroDePaginas(String paginas) {
        this.paginas = paginas;
    }

    @Override
    public String toString (){
        return this.getDia() + "," + this.getArquivo() + "," + this.getPaginas();
    }
}