package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientMain {

    /**
     * main method to start the client<br>
     * initializes the GUI chat frame<br>
     * connects a new client to the server
     */
    public static void main(String[] args) {
        ChatFrame frame = new ChatFrame();
        String[] ipPort = frame.getIpPort();
        String ipAddress = ipPort[0];
        int port = Integer.parseInt(ipPort[1]);

        Socket socket;
        try {
            socket = new Socket(ipAddress, port);
            String username = frame.getName();
            Client client = new Client(socket, username);

            frame.setClient(client);
            client.setChatFrame(frame);

            client.isNameAvailable();
            client.listenForMessage();
        } catch (UnknownHostException e) {
            frame.serverErrorFrame("Unknown host");
        } catch (ConnectException e) {
            frame.serverErrorFrame("Server closed");
        } catch (SocketException e) {
            frame.serverErrorFrame("Network is unreachable");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
