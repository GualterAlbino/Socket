import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        final String host = "127.0.0.1";  // IP do servidor
        final int porta = 8080;  // Porta do servidor

        // Garente a abertura e o fechamento do socket
        try (DatagramSocket socket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName(host), porta))) {

            System.out.println("Servidor iniciado...");

            byte[] buffer = new byte[1000];

            while (true) {
                // Recebe o pacote de dados
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                // Converte os dados para String
                String receivedData = new String(requestPacket.getData(), 0, requestPacket.getLength());
                System.out.println("Dados recebidos: " + receivedData);

                // Cria uma nova thread para processar o cálculo
                Thread calcThread = new Thread(() -> {
                    try {
                        // Converte a String para a matriz (row e column)
                        Matriz matriz = converterStringParaMatriz(receivedData);

                        // Inicia o cálculo na nova thread
                        System.out.println("Iniciando os cálculos com os parâmetros: " + receivedData);

                        // Calcula o resultado
                        double resultado = newRowCalculate(matriz.linha, matriz.coluna);

                        // Loga o resultado
                        System.out.println("Resultado dos cálculos: " + resultado);

                        // Retorna a resposta do cálculo
                        String response = "Resultado do cálculo: " + resultado;
                        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.length(),
                                requestPacket.getAddress(), requestPacket.getPort());
                        socket.send(responsePacket);

                    } catch (IOException e) {
                        System.out.println("Erro ao enviar resposta: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erro no cálculo: " + e.getMessage());
                    }
                });

                // Inicia a thread
                calcThread.start();


            }

        } catch (SocketException e) {
            System.out.println("Erro no Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro de IO: " + e.getMessage());
        }
    }

    public static Matriz converterStringParaMatriz(String pMatrizString) {
        String[] partesMatriz = pMatrizString.split("\\|");

        String[] linhaString = partesMatriz[0].split(" ");
        var linha = new ArrayList<Double>();
        for (String pNum : linhaString) {
            linha.add(Double.parseDouble(pNum));
        }

        String[] colunaString = partesMatriz[1].split(" ");
        var coluna = new ArrayList<Double>();
        for (String pNum : colunaString) {
            coluna.add(Double.parseDouble(pNum));
        }

        return new Matriz(linha, coluna);
    }

    public static double newRowCalculate(List<Double> pLinha, List<Double> pColuna) {
        if (pLinha.size() == pColuna.size()) {
            double resultado = 0;
            for (int i = 0; i < pLinha.size(); i++) {
                resultado += pLinha.get(i) * pColuna.get(i);
            }
            return resultado;
        } else {
            throw new IllegalArgumentException("As linhas e as colunas devem ter o mesmo tamanho");
        }
    }
}


class Matriz {
    public List<Double> linha;
    public List<Double> coluna;

    public Matriz(List<Double> pLinha, List<Double> pColuna) {
        this.linha = pLinha;
        this.coluna = pColuna;
    }
}
