package ec.edu.ups.socket;

import java.io.*;
import java.net.*;
import java.util.*;

/* 
 * Este código implementa un servidor de chat que escucha en un puerto
 *  específico y permite que varios clientes se conecten.
 *   Cada cliente que se conecta se maneja en un hilo separado (ClientHandler)
 *    para que el servidor pueda atender a múltiples clientes simultáneamente.
 *     Los mensajes enviados por cualquier cliente se retransmiten a todos los clientes
 *      conectados.
 * 
 * */
public class Servidor {
    private static final int PORT = 8080; // Puerto en el que el servidor escucha
    private static Set<PrintWriter> clientWriters = new HashSet<>(); // Conjunto de escritores para enviar mensajes a los clientes

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor Inicializado en el puerto " + PORT); // Muestra un mensaje indicando que el servidor se ha iniciado en un puerto específico

            while (true) {
                new ClientHandler(serverSocket.accept()).start(); // Acepta nuevas conexiones de clientes y crea un nuevo hilo para manejar cada cliente
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message); // Envía un mensaje a todos los clientes conectados
        }

        System.out.println(message); // Imprime el mensaje en la consola del servidor
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true); // Crea un escritor para enviar mensajes al cliente
                clientWriters.add(out); // Agrega el escritor al conjunto de escritores para enviar mensajes a todos los clientes

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Crea un lector para recibir mensajes del cliente
                String message;

                while ((message = in.readLine()) != null) {
                    Servidor.broadcastMessage(message); // Envía el mensaje recibido a todos los clientes
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clientWriters.remove(out); // Elimina el escritor del conjunto al desconectarse el cliente
                }
                try {
                    clientSocket.close(); // Cierra la conexión con el cliente
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
