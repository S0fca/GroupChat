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

        this.pack();
    }

    private JPanel addChatPanel() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(text,BorderLayout.LINE_START);
        text.setEditable(false);
        text.setBorder(BorderFactory.createLineBorder(Color.white,5));
        text.setText("Welcome to the group chat!\n");

        textPanel.setBackground(Color.white);
        textPanel.setBorder(BorderFactory.createLineBorder(Color.white,5));

        JScrollPane scrollBar = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollBar.setPreferredSize(new Dimension(500,300));
        panel.add(scrollBar, BorderLayout.PAGE_START);
        panel.add(textField, BorderLayout.PAGE_END);
        textField.setBorder(BorderFactory.createLineBorder(Color.lightGray,2));
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
