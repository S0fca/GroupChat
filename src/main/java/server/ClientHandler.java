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
    private boolean notKicked = true;
    private boolean nameTaken = false;
    private final HashMap<String, CommandInterface> map = new HashMap<>();

    /**
     * sets up the input and output streams for communication<br>
     * checks if name available<br>
     * adds client handler<br>
     * informs everyone a client has connected
     *
     * @param socket the socket representing the client's connection
     */
    public ClientHandler(Socket socket) {
        commandsInitialization();
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            checkNameAvailability();

            if (!nameTaken && (clientUsername != null)) {
                clientHandlers.add(this);
                System.out.println(clientUsername + " has connected");
                Message message = new Message(clientUsername + " has entered the chat!", Type.SERVER_TEXT);
                broadcastMessage(message);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    /**
     * checks if name is available
     * informs the client about it
     */
    private void checkNameAvailability() {
        try {
            clientUsername = bufferedReader.readLine();
            nameTaken = clientHandlers.stream().anyMatch(clientHandler -> clientUsername.equals(clientHandler.getClientUsername()));

            if (nameTaken) {
                bufferedWriter.write("NameTaken");
            } else {
                bufferedWriter.write("NameAvailable");
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    /**
     * sends a message from a client to all other clients
     *
     * @param messageToSend a message from the client
     */
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

    /**
     * sends a private message to a client from another client
     *
     * @param privateMessage message to send
     */
    private void privateMessage(Message privateMessage) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equalsIgnoreCase(privateMessage.getSentTo())) {
                    clientHandler.bufferedWriter.write(privateMessage.toJson());
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
        if (notKicked && !nameTaken && clientUsername != null) {
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

    /**
     * informs all clients that this client was kicked out<br>
     * tells the client they've been kicked out and removes this ClientHandler from the list
     */
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

    /**
     * waits for Messages in json, parses them into a Message object <br>
     * processes the message
     */
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

                switch (message.getType()) {
                    case PRIVATE_MESSAGE -> privateMessage(message);
                    case COMMAND -> {
                        try {
                            message.setText(map.get(message.getText()).execute());
                        } catch (NullPointerException e) {
                            message.setText("Invalid command");
                        }
                        privateMessage(message);
                    }
                    default -> broadcastMessage(message);
                }
            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    /**
     * initializes client commands
     */
    public void commandsInitialization() {
        map.put("commands", new Commands(map));
        map.put("clients", new Clients());
    }

    public String getClientUsername() {
        return clientUsername;
    }
}










