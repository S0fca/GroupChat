package server;

import server.commands.Clients;
import server.commands.CommandInterface;
import server.commands.Commands;
import server.commands.Kick;

import java.util.HashMap;
import java.util.Scanner;

public class Console {

    private boolean exit = false;
    private final HashMap<String, CommandInterface> map = new HashMap<>();

    public void initialization() {
        map.put("commands", new Commands(map));
        map.put("clients", new Clients());
        map.put("kick", new Kick());
    }

    private void execute() {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        command = command.trim().toLowerCase();
        if (map.containsKey(command)) {
            System.out.println(map.get(command).execute());
            exit = map.get(command).exit();
        } else System.out.println("Unknown command");
    }

    public void start() {
        initialization();
        do {
            execute();
        } while (!exit);
    }

}
