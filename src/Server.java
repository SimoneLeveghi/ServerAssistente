import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    private ServerSocket server;
    private Socket client;
    private final int porta;

    public Server() {
        server = null;
        client = null;
        porta = 1234;
    }
    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        @Override
        public void run () {
            try {
                while (true) {
                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());

                    String message = clientInput.readLine();
                    System.out.println("Messaggio ricevuto da client: " + message);
                    switch(message.toLowerCase()) {
                        case "data":
                            serverOutput.writeBytes("La data di oggi e' " + new Date() + "\n");
                            break;

                        case "meteo":
                            serverOutput.writeBytes("Oggi e' soleggiato " + "\n");
                            break;

                        case "saluto":
                            serverOutput.writeBytes("Ciao! Come posso aiutarti? " + "\n");
                            break;

                        default:
                            serverOutput.writeBytes("Scusa, non conosco questo comando. " + "\n");
                    }
                    serverOutput.flush();
                }
            } catch (IOException e) {
                System.out.println("Comunicazione interrotta con il client.");
            }
        }
    }

    public void attendi() {
        try {
            server = new ServerSocket(porta);
            server.setReuseAddress(true);
            System.out.println("Server in attesa di connessioni.");

            while(true) {
                client = server.accept();
                System.out.println("Nuova connessione: " + client);

                Thread clientHandler = new Thread(new ClientHandler(client));
                clientHandler.start();
            }

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Errore durante l'istanza del server.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.attendi();
    }
}