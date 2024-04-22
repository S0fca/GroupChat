package server;

import server.commands.*;

import java.util.HashMap;
import java.util.Scanner;

public class Console {

    private final HashMap<String, CommandInterface> map = new HashMap<>();

    public void initialization() {
        map.put("commands", new Commands(map));
        map.put("clients", new Clients());
        map.put("kick", new Kick());
        map.put("stop server", new Stop());
    }

    private void execute() {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        command = command.trim().toLowerCase();
        if (map.containsKey(command)) {
            System.out.println(map.get(command).execute());
        } else System.out.println("Unknown command");
    }

    public void start() {
        initialization();
        while (true) {
            execute();
        }
    }

}
