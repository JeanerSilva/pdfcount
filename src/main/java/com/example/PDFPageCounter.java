package com.example;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFPageCounter {

  public static Double paginas = 0.0;
  public static Integer arquivosTotais = 0;
  public static Integer totalDeSemanasInt = 20;
  private static File selectedDirectory = null;
  private static int diasPorSemana = 6;
  private static List<PDFDeEstudo> pdfsDeEstudo = new ArrayList<>();
  private static List<PDFParaCSV> pdfsParaCSV = new ArrayList<>();

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        createAndShowGUI();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private static void createAndShowGUI()  {
    JFrame frame = new JFrame("Contador de Páginas PDF personalizado para Mariana");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setSize(new Dimension(1200, 600));

    JLayeredPane layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(new Dimension(200, 400));

    JPanel panel = new JPanel(new BorderLayout());
    JTextArea textArea = new JTextArea();
    JLabel diretorio = new JLabel("Selecione um diretório");

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
    );

    textArea.setFont(new Font("Arial", Font.PLAIN, 14));
    textArea.setBounds(5, 100, 1170, 570);
    textArea.setEditable(false);
    diretorio.setBounds(5, 5, 200, 50);
    diretorio.setFont(new Font("Arial", Font.BOLD, 16));
    frame.setLocationRelativeTo(null);
    panel.add(diretorio, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT,
      layeredPane,
      panel
    );
    frame.add(splitPane);

    JButton buttonSelecionaDiretorio = new JButton("Selecionar Diretório");
    JButton buttonProcurar = new JButton("Contar páginas");
    JButton buttonLimpar = new JButton("Limpar");

    buttonSelecionaDiretorio.setBounds(30, 50, 150, 50);
    buttonProcurar.setBounds(30, 110, 150, 50); 
    buttonLimpar.setBounds(30, 170, 150, 50);

    Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7 };
    
    JComboBox<Integer> numberComboBox = new JComboBox<>(numbers);
    JLabel diasPorSemanaLabel = new JLabel("Dias de estudo por semana");
    numberComboBox.setBounds(30, 260, 50, 20);
    diasPorSemanaLabel.setBounds(30, 230, 200, 20);
    if (diasPorSemana >= 1 && diasPorSemana <= 7) {
      numberComboBox.setSelectedItem(diasPorSemana);
    }

    JLabel totaldeSemanaLabel = new JLabel("Total de semanas");
    totaldeSemanaLabel.setBounds(30, 290, 200, 20);
    JTextField totalDeSemanas = new JTextField(20);
    totalDeSemanas.setBounds(30, 320, 50, 20);


    layeredPane.add(buttonSelecionaDiretorio, JLayeredPane.DEFAULT_LAYER);
    layeredPane.add(buttonProcurar, JLayeredPane.PALETTE_LAYER);
    layeredPane.add(buttonLimpar, JLayeredPane.PALETTE_LAYER);
    layeredPane.add(diasPorSemanaLabel);
    layeredPane.add(numberComboBox);
    layeredPane.add(totaldeSemanaLabel);
    layeredPane.add(totalDeSemanas);
    buttonProcurar.setEnabled(false);
    buttonLimpar.setEnabled(false);
    totalDeSemanas.setText(String.valueOf(totalDeSemanasInt));

    buttonSelecionaDiretorio.addActionListener(e -> {
      selectedDirectory = selecionarDiretorio(frame);
      if (selectedDirectory != null) {
        buttonProcurar.setEnabled(true);
        buttonLimpar.setEnabled(true);
        diretorio.setText(selectedDirectory.getAbsolutePath());
      } else {
        buttonProcurar.setEnabled(false);
        buttonLimpar.setEnabled(false);
      }
    });

    buttonProcurar.addActionListener(e -> {
      new Thread(() -> {    
        textArea.setText("");
        procurarEPlotarArquivos(textArea);
      })
        .start();
    });

    buttonLimpar.addActionListener(e -> {
      textArea.setText("");
      selectedDirectory = null;
      buttonProcurar.setEnabled(false);
      buttonLimpar.setEnabled(false);
      diretorio.setText("Selecione um diretório");
    });

    numberComboBox.addActionListener(e ->
      diasPorSemana = (int) numberComboBox.getSelectedItem()
    );

    totalDeSemanas.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(final DocumentEvent paramDocumentEvent) {
        buttonProcurar.setEnabled(!totalDeSemanas.getText().isEmpty());
        totalDeSemanasInt = Integer.valueOf(totalDeSemanas.getText());
      }
  
      @Override
      public void insertUpdate(final DocumentEvent paramDocumentEvent) {
         buttonProcurar.setEnabled(!totalDeSemanas.getText().isEmpty());
         totalDeSemanasInt = Integer.valueOf(totalDeSemanas.getText());
      }
  
      @Override
      public void changedUpdate(final DocumentEvent paramDocumentEvent) {
         buttonProcurar.setEnabled(!totalDeSemanas.getText().isEmpty());
         totalDeSemanasInt = Integer.valueOf(totalDeSemanas.getText());
      }
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

  private static void procurarEPlotarArquivos(JTextArea textArea) {
    File rootFolder = new File(selectedDirectory.getAbsolutePath());
    arquivosTotais = 0;
    paginas = 0.0;
    processFolder(rootFolder, textArea);
    SwingUtilities.invokeLater(() ->
      textArea.append("\n\nTotal de arquivos: " + arquivosTotais + "\n")
    );
    SwingUtilities.invokeLater(() ->
      textArea.append("Total de paginas: " + paginas + "\n")
    );

    int diasDeLeitura = (totalDeSemanasInt * diasPorSemana);
    BigDecimal paginasPorDiaDecimal = BigDecimal.valueOf(paginas / diasDeLeitura);


    int paginasPorDia = paginasPorDiaDecimal
      .setScale(0, RoundingMode.CEILING)
      .intValue();

    SwingUtilities.invokeLater(() ->
      textArea.append(
        "Devem ser estudadas " +
        paginasPorDia +
        " paginas por dia, considerados " +
        diasPorSemana +
        " dias por semana, em um total de " +
        diasDeLeitura +
        " dias.\n"
      )
    );
    
    criarPlanoDeLeitura(pdfsDeEstudo, paginasPorDia, textArea);
    pdfsDeEstudo.clear();

    int ultimoIndice = selectedDirectory.getAbsolutePath().lastIndexOf("\\");   
    if (ultimoIndice == -1) {
        ultimoIndice = selectedDirectory.getAbsolutePath().lastIndexOf("/");       
    } 
    String nomeDoArquivo = ultimoIndice >= 0 ? selectedDirectory.getAbsolutePath().substring(ultimoIndice + 1) : selectedDirectory.getAbsolutePath();
    String barra = ultimoIndice != -1 ? "/" : "\\";
    String csvFilePath =  selectedDirectory + barra + nomeDoArquivo + ".csv";

    exportToCSV(pdfsParaCSV, csvFilePath, textArea);
  }

  public static void exportToCSV(List<PDFParaCSV> pdfsParaCSV2, String csvFilePath, JTextArea textArea) {
        try (FileWriter fileWriter = new FileWriter(csvFilePath);
             CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT)) {
            
            csvPrinter.printRecord("Dia", "Arquivo", "Paginas");

            for (PDFParaCSV pdf : pdfsParaCSV2) {
                csvPrinter.printRecord(pdf.getDia(), pdf.getArquivo(), pdf.getPaginas());
            }
            SwingUtilities.invokeLater(() ->
              textArea.append("\n\nExportação para CSV concluída com sucesso em " + csvFilePath)
            );
        } catch (IOException e) {
          SwingUtilities.invokeLater(() ->
              textArea.append("\n\nErro ao exportar para CSV: " + e.getMessage())
            );  

        }
    }

  private static void processFolder(File folder, JTextArea textArea) {

    File[] listOfFiles = folder.listFiles();
    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        if (file.isFile() && file.getName().endsWith(".pdf")) {
          processPDF(file, textArea);
        } else if (file.isDirectory()) {
          SwingUtilities.invokeLater(() ->
            textArea.append("\n\n Pasta " + file.getName() + ". Arquivos totais: " + arquivosTotais + ". Páginas totais: " + paginas + "\n\n")
          );
          processFolder(file, textArea); 
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
        
        pdfsDeEstudo.add(new PDFDeEstudo(file.getAbsolutePath(), pageCount));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void criarPlanoDeLeitura(
    List<PDFDeEstudo> pdfs,
    int paginasPorDia,
    JTextArea textArea
  ) {

    int diaEmCurso = 0;

    final int[] dia = { 1 };
    dia[0] = 1;
    final int[] indicePDFAtual = { 1 };
    indicePDFAtual[0] = 0;
    final int[] paginaAtualNoPDF = { 1 };
    paginaAtualNoPDF[0] = 1;

    while (indicePDFAtual[0] < pdfs.size()) {
      int paginasLidasNoDia = 0;
      String arquivo = "";
      while (
        paginasLidasNoDia < paginasPorDia && indicePDFAtual[0] < pdfs.size()
      ) {
        PDFDeEstudo pdfAtual = pdfs.get(indicePDFAtual[0]);
        int paginasRestantesNoPDF =
          pdfAtual.getNumeroDePaginas() - paginaAtualNoPDF[0] + 1;
        int paginasParaLerHoje = Math.min(
          paginasPorDia - paginasLidasNoDia,
          paginasRestantesNoPDF
        );

        
        String paginas = "";
        String diaDeLeitura = String.valueOf(dia[0]);
        
        if (diaEmCurso != dia[0]) {               
            String s = "\nDia " + dia[0] ;
            arquivo = " " + pdfAtual.getArquivo();    
          SwingUtilities.invokeLater(() ->
            textArea.append(s));
          diaEmCurso = dia[0];
        } else {
          arquivo = " " + pdfAtual.getArquivo();    
          SwingUtilities.invokeLater(() -> textArea.append(" e"));
        }
        String s1 =  " " +
            pdfAtual.getArquivo() +
            " paginas de " +
            paginaAtualNoPDF[0] +
            " a " +
            (paginaAtualNoPDF[0] + paginasParaLerHoje - 1);
        paginas += paginaAtualNoPDF[0] + " a " + (paginaAtualNoPDF[0] + paginasParaLerHoje - 1);

        SwingUtilities.invokeLater(() ->
          textArea.append(s1)
        );

        if (diaEmCurso != dia[0]) {
          SwingUtilities.invokeLater(() -> textArea.append("\n"));
          arquivo = "";
        }
        //System.out.println("dia: " + diaDeLeitura + ". arquivo: " + arquivo  + ". página: " + paginas);
        pdfsParaCSV.add(new PDFParaCSV(diaDeLeitura, arquivo, paginas));
        paginasLidasNoDia += paginasParaLerHoje;
        paginaAtualNoPDF[0] += paginasParaLerHoje;

        // Verificar se terminou o PDF atual
        if (paginaAtualNoPDF[0] > pdfAtual.getNumeroDePaginas()) {
          indicePDFAtual[0]++; // Passar para o próximo PDF
          paginaAtualNoPDF[0] = 1; // Começar do início do próximo PDF
        }
        
      }
      dia[0]++; // Passar para o próximo dia
    }
  }
}
