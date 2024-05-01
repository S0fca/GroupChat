package server.commands;

import server.ClientHandler;

import java.util.ConcurrentModificationException;
import java.util.Scanner;

public class Kick implements CommandInterface {

    @Override
    public String execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write name: ");
        String name = scanner.nextLine().trim();
        try {
            for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
                if (clientHandler.getClientUsername().equalsIgnoreCase(name)) {
                    clientHandler.kickPlayer();
                    return "User has been kicked out";
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        return "User not found";
    }
}
