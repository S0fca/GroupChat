import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            checkNameAvailability(bufferedWriter, bufferedReader);
            boolean add = true;
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.getClientUsername().equals(clientUsername)) {
                    add = false;
                    break;
                }
            }
            if (add) {
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
                    closeEverything();
                    break;
                }
            }
            bufferedWriter.write("NameAvailable");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println(3);
            closeEverything();
        }
    }

    private void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.equals(this)) {//!clientHandler.clientUsername.equals(clientUsername)
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    public void removeClientHandler() {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getClientUsername().equals(clientUsername) && clientHandler != this) {
                clientHandlers.remove(this);
                return;
            }
        }
        broadcastMessage("SERVER: " + clientUsername + " has left the chat\n");
    }

    private void closeEverything() {
        removeClientHandler();
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
                broadcastMessage(messageFromClient);
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
