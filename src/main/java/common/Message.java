package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class Message {

    private String text;
    private final Type type;
    private String sentTo;
    private String sentFrom;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructs a message object
     *
     * @param text     text of the message
     * @param type     type of the message
     * @param sentTo   recipient
     * @param sentFrom sender
     */
    public Message(String text, Type type, String sentTo, String sentFrom) {
        this.text = text;
        this.type = type;
        this.sentTo = sentTo;
        this.sentFrom = sentFrom;
    }

    /**
     * Constructs a message object
     *
     * @param text     text of the message
     * @param type     type of the message
     * @param sentFrom sender
     */
    public Message(String text, Type type, String sentFrom) {
        this.text = text;
        this.type = type;
        this.sentFrom = sentFrom;
    }

    /**
     * Constructs a message object
     *
     * @param text text of the message
     * @param type type of the message
     */
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

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", type=" + type +
                ", sentTo='" + sentTo + '\'' +
                ", sentFrom='" + sentFrom + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return text.equals(message.text) && type == message.type && Objects.equals(sentTo, message.sentTo) && Objects.equals(sentFrom, message.sentFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type, sentTo, sentFrom);
    }
}
