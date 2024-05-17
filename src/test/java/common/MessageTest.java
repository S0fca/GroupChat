package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {

    Message message = new Message("text", Type.MESSAGE, "user");

    @org.junit.jupiter.api.Test
    void toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        assertEquals(gson.toJson(message, Message.class), message.toJson());
    }

    @org.junit.jupiter.api.Test
    void fromJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String messageJson = gson.toJson(message);
        assertEquals(message.toString(), message.fromJson(messageJson).toString());
    }
}