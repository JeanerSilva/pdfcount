package com.example;

import java.util.ArrayList;
import java.util.List;

public class PlanoDeLeitura {

  public static void main(String[] args) {
    // Criando a lista de PDFs
    List<PDFDeEstudo> pdfs = new ArrayList<>();
    pdfs.add(new PDFDeEstudo("a (1)", 52));
    pdfs.add(new PDFDeEstudo("a (2)", 52));
    pdfs.add(new PDFDeEstudo("a (3)", 52));
    pdfs.add(new PDFDeEstudo("a (4)", 52));
 

    // Plano de leitura
    criarPlanoDeLeitura(pdfs, 30); // 5 páginas por dia
  }

  public static void criarPlanoDeLeitura(
    List<PDFDeEstudo> pdfs,
    int paginasPorDia
  ) {
    int diaEmCurso = 0;
    
    int dia = 1;
    int paginaAtualNoPDF = 1;
    int indicePDFAtual = 0;

    while (indicePDFAtual < pdfs.size()) {
      int paginasLidasNoDia = 0;
      while (
        paginasLidasNoDia < paginasPorDia && indicePDFAtual < pdfs.size()
      ) {
        PDFDeEstudo pdfAtual = pdfs.get(indicePDFAtual);
        int paginasRestantesNoPDF =
          pdfAtual.getNumeroDePaginas() - paginaAtualNoPDF + 1;
        int paginasParaLerHoje = Math.min(
          paginasPorDia - paginasLidasNoDia,
          paginasRestantesNoPDF
        );

        if (diaEmCurso != dia) {
          System.out.printf("\n\nDia " + dia);
          diaEmCurso = dia;
        } else {
          System.out.printf(" e ");
        }

        System.out.printf(
          " %s páginas de %d a %d ",
          pdfAtual.getNomeDoArquivo(),
          paginaAtualNoPDF,
          paginaAtualNoPDF + paginasParaLerHoje - 1
        );

        if (diaEmCurso != dia) {
          System.out.printf("\n");
        }

        paginasLidasNoDia += paginasParaLerHoje;
        paginaAtualNoPDF += paginasParaLerHoje;

        // Verificar se terminou o PDF atual
        if (paginaAtualNoPDF > pdfAtual.getNumeroDePaginas()) {
          indicePDFAtual++; // Passar para o próximo PDF
          paginaAtualNoPDF = 1; // Começar do início do próximo PDF
        }
      }
      dia++; // Passar para o próximo dia
    }
  }
}
/*

public static void criarPlanoDeLeitura(List<PDFDeEstudo> pdfs, int paginasPorDia) {
        int dia = 1;
        int paginaAtualNoPDF = 1;
        int indicePDFAtual = 0;
        
        while (indicePDFAtual < pdfs.size()) {
            PDFDeEstudo pdfAtual = pdfs.get(indicePDFAtual);
            int paginasRestantesNoPDF = pdfAtual.getNumeroDePaginas() - paginaAtualNoPDF + 1;
            int paginasParaLerHoje = Math.min(paginasPorDia, paginasRestantesNoPDF);

            System.out.printf("Dia %d: %s páginas de %d a %d\n", dia, pdfAtual.getArquivo(), paginaAtualNoPDF, paginaAtualNoPDF + paginasParaLerHoje - 1);

            paginaAtualNoPDF += paginasParaLerHoje;

            // Verificar se terminou o PDF atual
            if (paginaAtualNoPDF > pdfAtual.getNumeroDePaginas()) {
                indicePDFAtual++; // Passar para o próximo PDF
                paginaAtualNoPDF = 1; // Começar do início do próximo PDF
            }

            // Verificar se ainda há páginas para ler hoje
            if (paginasParaLerHoje == paginasPorDia) {
                dia++; // Passar para o próximo dia
            }
        }
    } 


 */
