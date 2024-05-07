package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

    private String text;
    private final Type type;
    private String sentTo;
    private String sentFrom;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Message(String text, Type type, String sentTo, String sentFrom) {
        this.text = text;
        this.type = type;
        this.sentTo = sentTo;
        this.sentFrom = sentFrom;
    }

    public Message(String text, Type type, String sentFrom) {
        this.text = text;
        this.type = type;
        this.sentFrom = sentFrom;
    }

    public Message(String text, Type type) {
        this.text = text;
        this.type = type;
    }

    /**
     * converts the Message object to json
     *
     * @return json message
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * parses a json string representing a Message object and constructs the Message
     *
     * @param jsonMessage json string representing the Message object
     * @return message object parsed from json
     */
    public static Message fromJson(String jsonMessage) {
        return gson.fromJson(jsonMessage, Message.class);
    }

    public String getSentFrom() {
        return sentFrom;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setText(String text) {
        this.text = text;
    }

}
