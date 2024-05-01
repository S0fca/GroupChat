package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {

    private String text;
    private Type type;
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

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", type=" + type +
                ", sentTo='" + sentTo + '\'' +
                ", sentFrom='" + sentFrom + '\'' +
                '}';
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Message fromJson(String json) {
        return gson.fromJson(json, Message.class);
    }
}
