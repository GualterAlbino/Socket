import java.io.*;
import java.net.*;

public class Client {
    private final int porta;
    private final String host;
    private final int clienteId;

    public Client(int pClienteId, String pHost, int pPorta) {
        this.host = pHost;
        this.porta = pPorta;
        this.clienteId = pClienteId;
    }

    // Método principal de execução do cliente
    public void execute() {
        // Matrizes de exemplo para multiplicação
        double[][] matrizA = {
                {2, 2},
                {2, 2}
        };

        double[][] matrizB = {
                {2, 2},
                {2, 2}
        };

        // Criação das combinações entre linhas de matrizA e colunas de matrizB
        String[] matrixCombinations = criarMatrizCombinada(matrizA, matrizB);

        // Pega dimensões das matrizes para gerar a matriz resultante
        int linhasMatrizA = matrizA.length;
        int colunasMatrizB = matrizB[0].length;

        // Matriz que armazenará o resultado da multiplicação
        double[][] matrizResultante = new double[linhasMatrizA][colunasMatrizB];

        // Índice que rastreia qual combinação de matrizes estamos processando
        int indiceCombinacao = 0;

        try {
            DatagramSocket socket = new DatagramSocket();  // Criação do socket UDP
            InetAddress serverAddress = InetAddress.getByName(host);  // Endereço do servidor

            // Loop que envia cada combinação de matriz e recebe o resultado
            for (int i = 0; i < linhasMatrizA; i++) {
                for (int j = 0; j < colunasMatrizB; j++) {
                    String matrixString = matrixCombinations[indiceCombinacao];  // Pega a combinação
                    indiceCombinacao++;

                    byte[] sendBuffer = matrixString.getBytes();  // Converte a combinação para bytes

                    // Envia o pacote com a combinação para o servidor
                    DatagramPacket requestPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, porta);
                    socket.send(requestPacket);

                    // Recebe a resposta do servidor
                    byte[] receiveBuffer = new byte[1000];
                    DatagramPacket responsePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(responsePacket);

                    // Processa a resposta recebida e armazena na matriz resultante
                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    double resultado = Double.parseDouble(response.replace("Resultado do cálculo: ", "").trim());

                    matrizResultante[i][j] = resultado;


                    // Sincroniza o log da thread com a identificação do cliente e o resultado
                    synchronized (System.out) {
                        System.out.println("Cliente " + clienteId + " - Resultado calculado: " + resultado + " para posição [" + i + "][" + j + "]");
                    }
                }
            }

            // Após receber todos os resultados, exibe a matriz completa
            synchronized (System.out) {
                //Garante que apenas uma thread por vez tenha acesso ao contexto
                System.out.println("\nCliente " + clienteId + " - Matriz resultante:");
                printarMatriz(matrizResultante);
            }



            /* SEM SINCRONIZAR
            System.out.println("\nCliente " + clienteId + " - Matriz resultante:");
            printarMatriz(matrizResultante);
            */

            /*
            synchronized (System.out) {
                System.out.println("\nCliente " + clienteId + " - Matriz resultante:");
                printarMatriz(matrizResultante);
            }
            */

            // Fecha o socket
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro no Cliente " + clienteId + ": " + e.getMessage());
        }
    }

    /**
     * Cria as combinações entre cada linha da matrizA e cada coluna da matrizB.
     * A combinação é representada no formato "linhaMatrizA | colunaMatrizB".
     *
     * @param pMatrizA Matriz A
     * @param pMatrizB Matriz B
     * @return Array de combinações
     */
    public static String[] criarMatrizCombinada(double[][] pMatrizA, double[][] pMatrizB) {
        int linhasMatrizA = pMatrizA.length;
        int colunasMatrizA = pMatrizA[0].length;
        int colunasMatrizB = pMatrizB[0].length;

        // Valida se as matrizes podem ser multiplicadas
        if (colunasMatrizA != pMatrizB.length) {
            throw new IllegalArgumentException("O número de colunas da primeira matriz deve ser igual ao número de linhas da segunda matriz.");
        }

        // Array de combinações (linha da matriz 1 e coluna da matriz 2)
        String[] combinacoes = new String[linhasMatrizA * colunasMatrizB];
        int index = 0;

        // Gera as combinações
        for (int i = 0; i < linhasMatrizA; i++) {
            for (int j = 0; j < colunasMatrizB; j++) {

                StringBuilder rowString = new StringBuilder();

                for (int k = 0; k < colunasMatrizA; k++) {
                    rowString.append(pMatrizA[i][k]);
                    if (k < colunasMatrizA - 1) {
                        rowString.append(" ");
                    }
                }

                StringBuilder colString = new StringBuilder();
                for (int k = 0; k < pMatrizB.length; k++) {
                    colString.append(pMatrizB[k][j]);
                    if (k < pMatrizB.length - 1) {
                        colString.append(" ");
                    }
                }

                // Adiciona a combinação ao array no formato "linhaMatrizA | colunaMatrizB"
                combinacoes[index] = rowString + "|" + colString;
                index++;
            }
        }

        return combinacoes;
    }

    /**
     * Função para imprimir a matriz de forma organizada e esteticamente agradável.
     * Cada valor será alinhado com espaçamento apropriado e exibido por linhas e colunas.
     *
     * @param matrix Matriz a ser impressa
     */
    public static void printarMatriz(double[][] matrix) {
        for (double[] linha : matrix) {
            System.out.print("[ ");
            for (int j = 0; j < linha.length; j++) {
                System.out.printf("%.2f", linha[j]);  // Formata os números com duas casas decimais
                if (j < linha.length - 1) {
                    System.out.print(" , ");
                }
            }
            System.out.println(" ]");
        }
    }
}
