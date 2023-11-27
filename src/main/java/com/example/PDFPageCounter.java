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
  private static File selectedDirectory = null;

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
    JFrame frame = new JFrame("Contador de Páginas PDF");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(1200, 600));

    JLayeredPane layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(new Dimension(200, 400));

    JPanel panel = new JPanel(new BorderLayout());
    JTextArea textArea = new JTextArea();
    panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

    textArea.setEditable(true);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    );

    textArea.setFont(new Font("Arial", Font.PLAIN, 12));
    textArea.setBounds(30, 30, 1170, 570);
    frame.setLocationRelativeTo(null);
    panel.add(scrollPane);

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      layeredPane,
      panel
    );
    frame.add(splitPane);

    JButton buttonSelecionaDiretorio = new JButton("Selecionar Diretório");
    JButton buttonProcurar = new JButton("Contar páginas");
    JButton buttonLimpar = new JButton("Limpar");

    buttonSelecionaDiretorio.setBounds(10, 50, 150, 50); // x, y, largura, altura
    buttonProcurar.setBounds(10, 110, 150, 50); // x, y, largura, altura
    buttonLimpar.setBounds(10, 170, 150, 50 );
   

    layeredPane.add(buttonSelecionaDiretorio, JLayeredPane.DEFAULT_LAYER);
    layeredPane.add(buttonProcurar, JLayeredPane.PALETTE_LAYER);
    layeredPane.add(buttonLimpar, JLayeredPane.PALETTE_LAYER);
    buttonProcurar.setEnabled(false);

    buttonSelecionaDiretorio.addActionListener(e -> {
      selectedDirectory = selecionarDiretorio(frame);
      if (selectedDirectory != null) {
        buttonProcurar.setEnabled(true); // Habilita o botão de ação
        buttonLimpar.setEnabled(true);
    } else {
         buttonProcurar.setEnabled(false);
         buttonLimpar.setEnabled(false);
    }
    });

    buttonProcurar.addActionListener(e -> {
      new Thread(() -> {
        SwingUtilities.invokeLater(() ->
          textArea.append(
            "\nProcurando arquivos no diretorio " +
            selectedDirectory.getAbsolutePath() +
            "...\n\n\n"
          )
        );
      })
        .start();
      new Thread(() -> {
         procurarEPlotarArquivos(textArea);
      })
        .start();
    });

    buttonLimpar.addActionListener(e -> {
        textArea.setText("");
        selectedDirectory = null;
        buttonProcurar.setEnabled(false);
        buttonLimpar.setEnabled(false);
    });

    frame.setVisible(true);
  }

  private static File selecionarDiretorio(JFrame frame) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int option = fileChooser.showOpenDialog(frame);
    if (option == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile();
    }
    return null;
  }

  private static void procurarEPlotarArquivos(
    JTextArea textArea
  ) {
    System.out.println("iniciando busca em " + selectedDirectory.getAbsolutePath());
    File rootFolder = new File(selectedDirectory.getAbsolutePath());
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
          System.out.println("Pasta " + file.getName() + "\n\n");
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
      SwingUtilities.invokeLater(() -> {
        textArea.append(
          file.getAbsolutePath() + " tem " + pageCount + " paginas.\n"
        );
        System.out.println( file.getAbsolutePath() + " tem " + pageCount + " paginas.\n");
    });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
