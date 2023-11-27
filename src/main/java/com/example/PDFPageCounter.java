package com.example;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import javax.swing.*;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFPageCounter {

  public static Double paginas = 0.0;
  public static Integer arquivosTotais = 0;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        createAndShowGUI();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    });
  }

  private static void createAndShowGUI() throws URISyntaxException {
    JFrame frame = new JFrame("PDF Page Counter");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(1200, 600));

    JTextArea textArea = new JTextArea("");
    textArea.setEditable(true);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    );

    textArea.setFont(new Font("Arial", Font.PLAIN, 12));
    textArea.setBounds(30, 30, 1170, 570);
    frame.add(scrollPane);

    String jarPath =
      PDFPageCounter.class.getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .toURI()
        .getPath();
    File jarFile = new File(jarPath);
    String jarDir = jarFile.getParent();
    frame.setVisible(true);

    SwingUtilities.invokeLater(() ->
      textArea.append(
        "\nProcurando arquivos no diretorio " + jarDir + "...\n\n\n"
      )
    );

    new Thread(() -> {
      procurarePlotarArquivos(textArea, jarDir);
    })
      .start();
  }

  private static void procurarePlotarArquivos(
    JTextArea textArea,
    String jarDir
  ) {
    File rootFolder = new File(jarDir);
    processFolder(rootFolder, textArea);
    SwingUtilities.invokeLater(() ->
      textArea.append("\n\nTotal de arquivos " + arquivosTotais + "\n")
    );
    SwingUtilities.invokeLater(() ->
      textArea.append("Total de paginas " + paginas + "\n")
    );
    BigDecimal valorArredondado = BigDecimal
      .valueOf(paginas / 7)
      .setScale(2, RoundingMode.HALF_UP);
    SwingUtilities.invokeLater(() ->
      textArea.append(
        "Devem ser estudadas " +
        valorArredondado +
        " paginas por dia, considerados 7 dias por semana.\n"
      )
    );
  }

  private static void processFolder(File folder, JTextArea textArea) {
    File[] listOfFiles = folder.listFiles();

    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        if (file.isFile() && file.getName().endsWith(".pdf")) {
          processPDF(file, textArea);
        } else if (file.isDirectory()) {
          SwingUtilities.invokeLater(() ->
            textArea.append("\n\n Pasta " + file.getName() + "\n\n")
          );
          processFolder(file, textArea); // Chamada recursiva para subdiretórios
        }
      }
    }
  }

  private static void processPDF(File file, JTextArea textArea) {
    try (PDDocument document = PDDocument.load(file)) {
      int pageCount = document.getNumberOfPages();
      paginas += pageCount;
      arquivosTotais++;
      SwingUtilities.invokeLater(() ->
        textArea.append(
          file.getAbsolutePath() + " tem " + pageCount + " paginas.\n"
        )
      );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
