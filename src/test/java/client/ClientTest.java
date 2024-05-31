package client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClientTest {

    public static final String MESSAGE = "hello";
    public static final String USERNAME = "user";
    private StringBuilder out;

    @Mock
    Socket socket;

    Client client;

    @BeforeEach
    void beforeEach(@Mock InputStream in) throws IOException {
        out = new StringBuilder();
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) {
                out.append((char) b);
            }
        };

        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(in);
        client = new Client(socket, USERNAME);
    }

    @Test
    void sendMessages() {
        out = new StringBuilder();
        client.sendMessages(MESSAGE);
        assertEquals(MESSAGE + "\r\n", out.toString());
    }

    @Test
    void sendUsername() {
        out = new StringBuilder();
        client.sendUsername();
        assertEquals(USERNAME + "\r\n", out.toString());
    }
}

