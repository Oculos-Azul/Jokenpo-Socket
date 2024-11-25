package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;

    public Server(ServerSocket server) {
        this.server = server;
    }

    /**
     * Inicia o server criado
     */
    public void iniciaServer() {
        try {

            // Mantém o server aberto até que o host o desligue:
            while (!server.isClosed()) {
                System.out.println("Servidor iniciado na porta: " + server.getLocalPort());

                // Aceita a conexão de um jogador ao server
                Socket socket = server.accept();

                // Instância um novo handler
                UserHandler usuario = new UserHandler(socket);
                System.out.println(usuario.getNome() + " entrou na partida!");

                // Instância uma nova thread para cada jogador
                Thread thread = new Thread(usuario);
                thread.start();

            }
        } catch (IOException e) {
            fechaServer();
        }
    }

    /**
     * Fecha o servidor
     */
    public void fechaServer() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instânca e roda um novo servidor.
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {

        Server server = new Server(new ServerSocket(7107));
        server.iniciaServer();

    }
}