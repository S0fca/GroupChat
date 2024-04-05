package client;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private ChatFrame chatFrame;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            setUsername(username);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendUsername();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void setUsername(String username) {
        this.username = String.valueOf(username.charAt(0)).toUpperCase() + username.substring(1);
    }

    public Client() {
    }


    public void isNameAvailable() {
        new Thread(() -> {
            String message;
            try {
                message = bufferedReader.readLine();
                if (message.equals("NameTaken")) {
                    chatFrame.nameTaken();
                    Thread.currentThread().interrupt();
                } else {
                    chatFrame.writeInMessage("Name is available :)");
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }

    public void sendMessages(String messageToSend) {
        try {
            bufferedWriter.write(username + ": " + messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void sendUsername() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String message;
            while (socket.isConnected()) {
                try {
                    message = bufferedReader.readLine();
                    chatFrame.writeInMessage(message);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
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

    public void sendFrame(ChatFrame frame) {
        chatFrame = frame;
    }
}
