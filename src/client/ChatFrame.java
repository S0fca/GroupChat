package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatFrame extends JFrame {

    private JPanel panel;
    final JTextArea text = new JTextArea();
    final JTextField textField = new JTextField();
    private Client client;

    public static void main(String[] args) {
        new ChatFrame();
    }

    public ChatFrame() {
        this.setTitle("GroupChat");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.add(addChatPanel());

        this.pack();
    }

    public ChatFrame(Client client) {
        this.client = client;
        this.setTitle("GroupChat");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.add(addChatPanel());

        this.setPreferredSize(new Dimension(300, 200));
        this.pack();
    }

    private JPanel addChatPanel() {
        panel = new JPanel(new BorderLayout());
        text.setEditable(false);
        text.setBorder(BorderFactory.createLineBorder(Color.white, 5));
        panel.add(text, BorderLayout.PAGE_START);
        text.setText("Welcome to the group chat!\n");
        panel.add(textField, BorderLayout.PAGE_END);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyTyped = e.getKeyChar();
                if (keyTyped == KeyEvent.VK_ENTER) {
                    sendMessage(textField.getText());
                    textField.setText("");
                }
            }
        });
        return panel;
    }

    public void writeInMessage(String message) {
        text.append(message + '\n');
    }

    public void sendMessage(String messageToSend) {
        text.append(messageToSend + '\n');
        client.sendMessages(messageToSend);
    }
}
