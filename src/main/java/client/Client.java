package client;

import common.Message;
import common.Type;

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

    public void isNameAvailable() {
        new Thread(() -> {
            String text;
            try {
                text = bufferedReader.readLine();

                if (text.equals("NameTaken")) {
                    chatFrame.nameTakenFrame();
                    Thread.currentThread().interrupt();
                } else {
                    chatFrame.writeInMessage(new Message(username + " is available :)", Type.SERVER_TEXT, "SERVER"));
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }

    public void sendMessages(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
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
            while (socket.isConnected()) {
                String text = "";
                try {
                    String s;
                    while (!(s = bufferedReader.readLine()).equals("}")) {
                        text += s + '\n';
                    }
                    text += "}";
                    chatFrame.writeInMessage(text);
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

    public Client() {
    }

    public String getUsername() {
        return username;
    }
}
