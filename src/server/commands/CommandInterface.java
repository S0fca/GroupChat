package server.commands;

public interface CommandInterface {

    String execute();

    default boolean exit() {
        return false;
    }

}
