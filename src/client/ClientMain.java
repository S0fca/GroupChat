package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = null;
        try {
            socket = new Socket("localhost", 1234);
        } catch (ConnectException e) {
            System.out.println("Server is closed :(");
            System.exit(0);
        }
        Client client = new Client(socket, username);
        ChatFrame frame = new ChatFrame(client);
        client.sendFrame(frame);

        client.isNameAvailable();
        client.listenForMessage();
    }
}
