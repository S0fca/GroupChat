package server;

import common.Message;
import common.Type;
import server.commands.Clients;
import server.commands.CommandInterface;
import server.commands.Commands;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientHandler implements Runnable {

    public static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private boolean nameTaken = false;
    private boolean notKicked = true;
    private final HashMap<String, CommandInterface> map = new HashMap<>();

    public ClientHandler(Socket socket) {
        initialization();
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
                Message message = new Message(clientUsername + " has entered the chat!", Type.SERVER_TEXT);
                broadcastMessage(message);
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

    private void broadcastMessage(Message messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.equals(this)) {
                    clientHandler.bufferedWriter.write(messageToSend.toJson());
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void privateMessage(Message message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equalsIgnoreCase(message.getSentTo())) {
                    clientHandler.bufferedWriter.write(message.toJson());
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
            Message message = new Message(clientUsername + " has left the chat", Type.SERVER_TEXT);
            broadcastMessage(message);
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
        Message message = new Message(clientUsername + " has been kicked out", Type.SERVER_TEXT);
        Message privateMessage = new Message("You've been kicked out", Type.PRIVATE_MESSAGE, clientUsername, "SERVER");
        privateMessage(privateMessage);
        broadcastMessage(message);

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
        while (socket.isConnected()) {
            String messageFromClient = "";

            try {
                String s;
                while (!(s = bufferedReader.readLine()).equals("}")) {
                    messageFromClient += s + '\n';
                }
                messageFromClient += "}";

                Message message = Message.fromJson(messageFromClient);

                if (message.getType().equals(Type.PRIVATE_MESSAGE)) {
                    privateMessage(message);
                } else if (message.getType().equals(Type.COMMAND)) {
                    try {
                        message.setText(map.get(message.getText()).execute());
                    } catch (NullPointerException e) {
                        message.setText("Invalid command");
                    }
                    privateMessage(message);
                } else {
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    public void initialization() {
        map.put("commands", new Commands(map));
        map.put("clients", new Clients());
    }

    public String getClientUsername() {
        return clientUsername;
    }

}
