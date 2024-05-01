package server.commands;


import java.util.HashMap;

public class Commands implements CommandInterface {

    private final HashMap<String, CommandInterface> map;

    public Commands(HashMap<String, CommandInterface> map) {
        this.map = map;
    }

    @Override
    public String execute() {
        String commands = "";
        for (String command : map.keySet()) commands += command + '\n';
        return commands.strip();
    }

}
