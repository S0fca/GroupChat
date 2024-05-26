package common;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {
    final Message message = new Message("text", Type.MESSAGE, "user");
    private final String messageJSON = """
                {
                    sentFrom: "user",
                    text: "text",
                    type: "MESSAGE"
                }
                """;

    @Test
    void toJson() throws JSONException {
        String actual = message.toJson();
        JSONAssert.assertEquals(messageJSON, actual, true);
    }

    @Test
    void fromJson() {
        Message actual = Message.fromJson(messageJSON);
        assertEquals(message, actual);
    }
}