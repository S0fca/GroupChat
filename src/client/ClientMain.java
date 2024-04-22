package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientMain {

    public static void main(String[] args) throws IOException {
        boolean serverRunning = true;

        ChatFrame frame = new ChatFrame();
        String[] ipPort = frame.getIpPort();
        String ipAddress = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        Socket socket = null;
        try {
            socket = new Socket(ipAddress, port);
        } catch (UnknownHostException e) {
            serverRunning = false;
            frame.serverErrorFrame("Unknown host");
        } catch (ConnectException e) {
            serverRunning = false;
            frame.serverErrorFrame("Server closed");
        } catch (SocketException e) {
            serverRunning = false;
            frame.serverErrorFrame("Network is unreachable");
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
