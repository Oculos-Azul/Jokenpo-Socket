package user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class User {

    private String nome;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public User(Socket socket, String nome) {
        try {
            this.socket = socket;

            // Converte o stream de bits em letras
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.nome = nome;

        } catch (IOException e) {
            fecharConexao(socket, reader, writer);
        }
    }

    /**
     * Método responsável por enviar mensagens para o servidor.
     */
    public void enviarMensagme() {
        try {

            writer.write(nome);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);

            // Enquanto o usuário estiver conectado no server:
            while (socket.isConnected()) {

                String message = scanner.nextLine();
                writer.write(nome + ": " + message);
                writer.newLine();
                writer.flush();

            }
        } catch (IOException e) {
            fecharConexao(socket, reader, writer);
        }
    }

    /**
     * Método responsável por receber mensagens do servidor. Age como thread para
     * não comprometer o funcionamento da aplicação.
     */
    public void receberMensagem() {
        new Thread(() -> {
            // Mensagem enviada no chat.
            String serverMessage;

            // Variável de controle para interromper o loop de forma única
            boolean shouldClose = true;

            // Enquanto houver conexão:
            while (socket.isConnected() && shouldClose) {
                try {
                    // Lendo a mensagem enviada no chat.
                    serverMessage = reader.readLine();

                    // Se não estiver recebendo mais mensagens.
                    if (serverMessage == null) {
                        fecharConexao(socket, reader, writer);
                        System.out.println("\nConexão encerrada com o host.");

                        // Marca para fechar o loop
                        shouldClose = false;
                    } else {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    fecharConexao(socket, reader, writer);

                    // Marca para fechar o loop
                    shouldClose = false;
                }
            }
        }).start();
    }

    /**
     * Encerra a conexão do client e fecha os objetos reader e writer.
     * 
     * @param socket
     *            Socket de conexão.
     * @param reader
     *            BufferedReader.
     * @param writer
     *            BufferedWriter.
     */
    public void fecharConexao(Socket socket, BufferedReader reader, BufferedWriter writer) {
        // Fechando o reader, writer e socket.
        try {
            if (reader != null)
                reader.close();

            if (writer != null)
                writer.close();

            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se conecta a um servidor.
     * 
     * @param args
     * @throws IOException
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print(" Insira um nome de usuário: ");

        String nome = scanner.nextLine();
        System.out.println();

        // Socket de conexão com o server.
        Socket socket = new Socket("localhost", 7107);
        User usuario = new User(socket, nome);

        usuario.receberMensagem();
        usuario.enviarMensagme();
    }
}