package ec.edu.ups.socket;

import java.io.*;
import java.net.*;
import java.util.*;


public class Servidor {
    private static final int PORT = 8080;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor Inicializado en el puerto " + PORT);

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
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
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(out);

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;

                while ((message = in.readLine()) != null) {
                    Servidor.broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clientWriters.remove(out);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

