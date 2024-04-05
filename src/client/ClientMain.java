package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class ClientMain {

    public static void main(String[] args) throws IOException {
        boolean serverRunning = true;
        ChatFrame frame = new ChatFrame();

        Socket socket = null;
        try {
            socket = new Socket("localhost", 1234);
        } catch (ConnectException e) {
            serverRunning = false;
            frame.serverError("Server closed");
        } catch (SocketException e) {
            serverRunning = false;
            frame.serverError("Network is unreachable");
        }

        if (serverRunning) {
            frame.setFrame();
            String username = frame.getName();

            Client client = new Client(socket, username);
            frame.setClient(client);

            client.sendFrame(frame);

            frame.setChatPanel();
            client.listenForMessage();
        }
    }
}
