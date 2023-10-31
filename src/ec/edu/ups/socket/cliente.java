package ec.edu.ups.socket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class cliente {
    private static final String SERVER_ADDRESS = "localhost"; // Dirección del servidor
    private static final int SERVER_PORT = 8080; // Puerto del servidor

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JFrame frame;
    private JTextField messageField;
    private JTextArea chatArea;
    private String clientName;

    public cliente(String name) {
        clientName = name;

        frame = new JFrame("Chat Cliente - " + clientName); // Crear la ventana del cliente
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configurar cierre de la ventana
        frame.setSize(400, 300); // Configurar tamaño de la ventana

        messageField = new JTextField(); // Campo de texto para escribir mensajes
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(); // Enviar mensaje al presionar "Enter"
            }
        });

        chatArea = new JTextArea(); // Área de chat para mostrar los mensajes
        chatArea.setEditable(false); // No se puede editar el área de chat
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setBackground(Color.ORANGE); // Color de fondo del área de chat

        JScrollPane scrollPane = new JScrollPane(chatArea); // Barra de desplazamiento para el área de chat
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(BorderLayout.CENTER, scrollPane); // Agregar el área de chat
        frame.getContentPane().add(BorderLayout.SOUTH, messageField); // Agregar el campo de texto

        setupNetworking(); // Configurar la conexión con el servidor

        frame.setVisible(true); // Mostrar la ventana
    }

    private void setupNetworking() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT); // Conectar al servidor
            out = new PrintWriter(socket.getOutputStream(), true); // Preparar para enviar mensajes al servidor
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Preparar para recibir mensajes del servidor

            Thread receiveThread = new Thread(new ReceiveMessage()); // Iniciar un hilo para recibir mensajes
            receiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText(); // Obtener el mensaje del campo de texto
            out.println(clientName + ": " + message); // Enviar mensaje al servidor con el nombre del cliente
            messageField.setText(""); // Limpiar el campo de texto
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ReceiveMessage implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n"); // Mostrar mensajes en el área de chat
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String name = JOptionPane.showInputDialog("Ingrese nombre de cliente:"); // Solicitar nombre de cliente
                new cliente(name); // Crear una instancia de cliente con el nombre ingresado
            }
        });
    }
}
