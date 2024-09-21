import java.io.*;
import java.net.*;

public class MultiClient {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1";  // Endereço do servidor
        final int SERVER_PORT = 8080;  // Porta do servidor

        // Número de clientes que você deseja simular
        int numClients = 5;

        // Cria múltiplos clientes rodando em threads separadas
        for (int i = 0; i < numClients; i++) {
            int clientId = i + 1; // Identificador único para cada cliente
            new Thread(() -> {
                Client client = new Client(clientId, SERVER_IP, SERVER_PORT);
                client.execute();
            }).start();
        }
    }
}
