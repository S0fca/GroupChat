package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Server {

    private final ServerSocket serverSocket;
    private static final Console console = new Console();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        int port = getPort();
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    private static void startConsole() {
        new Thread(console::start).start();
    }

    public void startServer() {
        System.out.println("Server started");
        startConsole();
        System.out.println("Console started\nWrite \"commands\" to se which commands you can use");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getPort() {
        int port;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write port: ");
        try {
            port = scanner.nextInt();
            if (port > 65535 || port < 0) {
                System.out.println("Write correct port.");
                port = getPort();
            }
        } catch (InputMismatchException e) {
            System.out.println("Write numbers pls");
            return getPort();
        }
        return port;
    }
}
