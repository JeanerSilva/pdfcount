package com.example;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // Redireciona os dados para a área de texto
        textArea.append(String.valueOf((char) b));
        // Garante que o último caractere seja sempre visível
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
