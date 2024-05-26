package client;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTest {

    private StringBuilder out = new StringBuilder();

    OutputStream outputStream = new OutputStream() {
        @Override
        public void write(int b) {
            out.append((char) b);
        }
    };

    Socket socket = new Socket() {
        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public InputStream getInputStream() {
            return new InputStream() {
                @Override
                public int read() {
                    return 0;
                }
            };
        }
    };

    Client client = new Client(socket, "user");

    @Test
    void sendMessages() {
        String message = "hello";
        out = new StringBuilder();
        client.sendMessages(message);
        assertEquals(message + "\r\n", out.toString());
    }
}

