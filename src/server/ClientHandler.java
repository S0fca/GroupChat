package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private boolean nameTaken = false;
    private boolean notKicked = true;


    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            checkNameAvailability(bufferedWriter, bufferedReader);
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.getClientUsername().equals(clientUsername)) {
                    nameTaken = true;
                    break;
                }
            }
            if (!nameTaken) {
                clientHandlers.add(this);
                broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void checkNameAvailability(BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            clientUsername = bufferedReader.readLine();
            for (ClientHandler client : clientHandlers) {
                if (client.getClientUsername().equals(clientUsername)) {
                    bufferedWriter.write("NameTaken");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    return;
                }
            }
            bufferedWriter.write("NameAvailable");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            System.out.println(clientUsername + " has connected");
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.equals(this)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void privateMessage(String messageToSend, String sentToUsername, String username) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equalsIgnoreCase(sentToUsername)) {
                    clientHandler.bufferedWriter.write(username + " private message: " + messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                    break;
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void closeEverything() {
        clientHandlers.remove(this);
        if (!nameTaken && notKicked) {
            broadcastMessage("SERVER: " + clientUsername + " has left the chat");
        }
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kickPlayer() {
        notKicked = false;
        privateMessage("You've been kicked out", clientUsername, clientUsername);
        broadcastMessage("SERVER: " + clientUsername + " has been kicked out");

        clientHandlers.remove(this);
        try {
            if (bufferedWriter != null) bufferedWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                String[] parts = messageFromClient.split(": ", 2);

                if (parts[1].charAt(0) == '@') {
                    privateMessage(parts[1].substring(parts[1].indexOf(' ') + 1), parts[1].split(" ")[0].substring(1), parts[0]);
                } else {
                    broadcastMessage(messageFromClient);
                }

            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    public String getClientUsername() {
        return clientUsername;
    }

}
