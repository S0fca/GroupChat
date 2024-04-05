package client;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatFrame {

    private JFrame frame;
    private final JTextPane chat = new JTextPane();
    private final JTextField textField = new JTextField();
    private Client client = new Client();
    private final StyledDocument doc = chat.getStyledDocument();


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

        writeBoldText("Welcome to the group chat!\n");
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
        boolean nameIncorrect;

        String username;
        do {
            nameIncorrect = false;
            JOptionPane.showMessageDialog(null, nameInput, "Enter your username for the group chat", JOptionPane.PLAIN_MESSAGE);

            username = nameInput.getText().strip();

            if (username.length() < 2) {
                nameIncorrect = true;
                JOptionPane.showMessageDialog(null, "Name must be at least 2 characters long", "Invalid Username", JOptionPane.ERROR_MESSAGE);
            }
            if (username.contains("\s")) {
                nameIncorrect = true;
                JOptionPane.showMessageDialog(null, "Name cannot include whitespace", "Invalid Username", JOptionPane.ERROR_MESSAGE);
            }

        } while (nameIncorrect);

        return username;
    }

    public void writeInMessage(String message) {
        String[] parts = message.split(": ");
        if (parts.length == 1) {
            writeText(message);
        } else {
            writeBoldText(parts[0] + ": ");
            writeText(parts[1]);
        }
    }

    private void writeText(String text) {
        try {
            doc.insertString(doc.getLength(), text + '\n', null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void writeBoldText(String text) {
        Style style = chat.addStyle("Style", null);
        StyleConstants.setBold(style, true);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String messageToSend) {
        try {
            doc.insertString(doc.getLength(), messageToSend + '\n', null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
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
