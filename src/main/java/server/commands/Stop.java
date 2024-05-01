package server.commands;

public class Stop implements CommandInterface{
    @Override
    public String execute() {
        System.out.println("Server stopped");
        System.exit(0);
        return null;
    }
}
