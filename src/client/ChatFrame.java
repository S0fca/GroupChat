package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatFrame {

    JFrame frame;
    final JTextArea chat = new JTextArea();
    final JTextField textField = new JTextField();
    private Client client = new Client();

    public void setFrame() {

        frame = new JFrame("GroupChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(chat, BorderLayout.LINE_START);
        chat.setEditable(false);
        chat.setBorder(BorderFactory.createLineBorder(Color.white, 5));

        textPanel.setBackground(Color.white);
        textPanel.setBorder(BorderFactory.createLineBorder(Color.white, 5));

        JScrollPane scrollBar = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollBar.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollBar, BorderLayout.PAGE_START);
        panel.add(textField, BorderLayout.PAGE_END);
        textField.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));

        frame.add(panel);
        frame.pack();
    }

    public void setChatPanel() {
        client.isNameAvailable();

        chat.setText("Welcome to the group chat!\n");
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
    }

    public String getName() {
        TextField nameInput = new TextField();

        String username;
        do {
            JOptionPane.showMessageDialog(null, nameInput, "Enter your username for the group chat", JOptionPane.PLAIN_MESSAGE);

            username = nameInput.getText().strip();

            if (username.length() < 2) {
                JOptionPane.showMessageDialog(null, "Name must be at least 2 characters long", "Invalid Username", JOptionPane.ERROR_MESSAGE);
            }
        } while (username.strip().length() < 2);

        return username;
    }

    public void writeInMessage(String message) {
        chat.append(message + '\n');
    }

    public void sendMessage(String messageToSend) {
        chat.append(messageToSend + '\n');
        client.sendMessages(messageToSend);
    }

    public void nameTaken() {
        frame.setVisible(false);
        JFrame nameTakenFrame = new JFrame("Name taken");
        nameTakenFrame.setLocationRelativeTo(null);
        nameTakenFrame.setResizable(false);
        nameTakenFrame.setVisible(true);

        JLabel label = new JLabel();
        label.setText("Name taken");
        label.setBorder(BorderFactory.createLineBorder(nameTakenFrame.getBackground(), 16));

        nameTakenFrame.add(label);
        nameTakenFrame.pack();
    }

    public void serverError(String message) {
        JFrame errorFrame = new JFrame(message);
        errorFrame.setLocationRelativeTo(null);
        errorFrame.setResizable(false);
        errorFrame.setVisible(true);

        JLabel label = new JLabel();
        label.setText(message);
        label.setBorder(BorderFactory.createLineBorder(errorFrame.getBackground(), 16));

        errorFrame.add(label);
        errorFrame.pack();
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
