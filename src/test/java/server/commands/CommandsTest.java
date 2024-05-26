package server.commands;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CommandsTest {

    private Commands commands;

    @Before
    public void before() {
        HashMap<String, CommandInterface> commandMap = new HashMap<>();
        commandMap.put("command1", mock(CommandInterface.class));
        commandMap.put("command2", mock(CommandInterface.class));
        commandMap.put("command3", mock(CommandInterface.class));

        commands = new Commands(commandMap);
    }

    @Test
    void execute() {
        String actual = commands.execute();

        String expected = "command1\ncommand2\ncommand3";

        assertEquals(expected, actual);
    }
}