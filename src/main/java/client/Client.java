package client;

import common.Message;
import common.Type;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private ChatFrame chatFrame;

    /**
     * constructs a new Client object with the provided socket and username<br>
     * initializes the client's socket, input and output streams, sets the username<br>
     * sends the username to the server
     *
     * @param socket   connection socket
     * @param username name to send
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            sendUsername();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * reads message from the server about name availability<br>
     * informs the user if the name is available
     */
    public void isNameAvailable() {
        new Thread(() -> {
            String text;
            try {
                text = bufferedReader.readLine();

                if (text.equals("NameTaken")) {
                    chatFrame.serverErrorFrame("Name taken");
                    Thread.currentThread().interrupt();
                } else if (text.equals("NameAvailable")){
                    chatFrame.setFrame();
                    chatFrame.writeInMessage(new Message(username + " is available :)", Type.SERVER_TEXT, "SERVER"));
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }

    /**
     * sends text to the server
     *
     * @param messageToSend text to send to the server
     */
    public void sendMessages(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * sends client username to the server
     */
    @VisibleForTesting
    protected void sendUsername() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * starts a new thread that waits for incoming messages in json<br>
     * calls method to write that message
     */
    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                String text = "";
                try {
                    String s;
                    while (!Objects.equals(s = bufferedReader.readLine(), "}")) {
                        text += s + '\n';
                    }
                    text += "}";
                    chatFrame.writeInMessage(text);
                } catch (IOException e) {
                    chatFrame.serverErrorFrame("Server turned off");
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setChatFrame(ChatFrame frame) {
        chatFrame = frame;
    }
}
