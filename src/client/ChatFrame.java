package client;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

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

        JScrollPane scrollBar = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollBar.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollBar, BorderLayout.PAGE_START);
        panel.add(textField, BorderLayout.PAGE_END);
        textField.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));

        frame.add(panel);
        frame.pack();
    }

    public void setChatPanel() {
        client.isNameAvailable();

        writeInBoldText("Welcome to the group chat!\n");
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyTyped = e.getKeyChar();
                if (keyTyped == KeyEvent.VK_ENTER) {
                    sendOutMessage(textField.getText());
                    textField.setText("");
                }
            }
        });
    }

    public String getName() {
        String usernameFile = "username";
        TextField nameInput = new TextField();
        nameInput.setText(read(usernameFile));
        boolean nameIncorrect;

        String username = "";
        do {
            nameIncorrect = false;
            int option = JOptionPane.showConfirmDialog(null, nameInput, "Enter your username for the group chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
            if (option == JOptionPane.OK_OPTION) {

                username = nameInput.getText().strip();

                if (username.length() < 2) {
                    nameIncorrect = true;
                    JOptionPane.showMessageDialog(null, "Name must be at least 2 characters long", "Invalid Username", JOptionPane.ERROR_MESSAGE);
                }
                if (username.contains("\s")) {
                    nameIncorrect = true;
                    JOptionPane.showMessageDialog(null, "Name cannot include whitespace", "Invalid Username", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.exit(0);
            }
        } while (nameIncorrect);
        write(username, usernameFile);
        return username;
    }

    public void writeInMessage(String message) {
        String[] parts = message.split(": ");
        if (parts.length == 1) {
            writeInText(message);
        } else {
            writeInBoldText(parts[0] + ": ");
            writeInText(parts[1]);
        }
    }

    private void writeInText(String text) {
        try {
            doc.insertString(doc.getLength(), text + '\n', null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void writeInBoldText(String text) {
        Style style = chat.addStyle("Style", null);
        StyleConstants.setBold(style, true);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendOutMessage(String messageToSend) {
        try {
            doc.insertString(doc.getLength(), messageToSend + '\n', null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        client.sendMessages(messageToSend);
    }

    public void nameTakenFrame() {
        frame.setVisible(false);
        JFrame nameTakenFrame = new JFrame("Name taken");
        nameTakenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameTakenFrame.setLocationRelativeTo(null);
        nameTakenFrame.setResizable(false);
        nameTakenFrame.setVisible(true);

        JLabel label = new JLabel();
        label.setText("Name taken");
        label.setBorder(BorderFactory.createLineBorder(nameTakenFrame.getBackground(), 16));

        nameTakenFrame.add(label);
        nameTakenFrame.pack();
    }

    public void serverErrorFrame(String message) {
        JFrame errorFrame = new JFrame(message);
        errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    public String[] getIpPort() {
        String ipFile = "ipAddress";
        String portFile = "port";
        boolean incorrectInput;
        JTextField ipAddressField = new JTextField();
        ipAddressField.setText(read(ipFile));
        JTextField portField = new JTextField();
        portField.setText(read(portFile));
        portField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
                if (portField.getText().length() > 5) {
                    e.consume();
                }
            }
        });
        Object[] message = {
                "IP Address:", ipAddressField,
                "Port:", portField
        };
        do {
            incorrectInput = false;
            int option = JOptionPane.showConfirmDialog(null, message, "Enter IP Address and Port", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    if (ipAddressField.getText().length() < 1 || portField.getText().length() < 1) {
                        incorrectInput = true;
                        JOptionPane.showMessageDialog(null, "Write Ip and Port", "Missing Ip or Port", JOptionPane.ERROR_MESSAGE);
                    } else if (Integer.parseInt(portField.getText()) > 65535) {
                        incorrectInput = true;
                        JOptionPane.showMessageDialog(null, "Port must be in range 0 to 65535", "Invalid Port", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ignored) {
                }
            } else {
                System.exit(0);
            }
        } while (incorrectInput);
        String ipAddress = ipAddressField.getText().strip();
        String port = portField.getText().strip();
        write(ipAddress, ipFile);
        write(port, portFile);
        return new String[]{ipAddress, port};
    }

    private void write(String text, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + ".txt"))) {
            bw.write(text);
        } catch (IOException e) {
            System.out.println("Something wrong with the file");
        }
    }

    private String read(String fileName) {
        String read = "";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName + ".txt"))) {
            read = br.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something wrong with the file");
        }
        return read;
    }
}
