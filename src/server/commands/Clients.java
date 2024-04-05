package server.commands;

import server.ClientHandler;

public class Clients implements CommandInterface {

    @Override
    public String execute() {
        String clients = "";
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            clients += clientHandler.getClientUsername() + '\n';
        }
        return ("Clients: \n" + clients).strip();
    }

}
