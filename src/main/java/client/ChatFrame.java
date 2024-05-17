package client;

import common.Message;
import common.Type;

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

    String clientUsername = "";
    private final JTextPane chat = new JTextPane();
    private final JTextField textField = new JTextField();
    private Client client;
    private final StyledDocument doc = chat.getStyledDocument();

    /**
     * adds components to the frame
     */
    public void setFrame() {
        JFrame frame = new JFrame("GroupChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.white);
        textPanel.setBorder(BorderFactory.createLineBorder(Color.white, 5));
        textPanel.add(chat, BorderLayout.LINE_START);

        chat.setEditable(false);
        chat.setBorder(BorderFactory.createLineBorder(Color.white, 5));
        textField.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));

        JScrollPane scrollBar = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollBar.setPreferredSize(new Dimension(500, 300));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.white);
        mainPanel.add(scrollBar, BorderLayout.PAGE_START);
        mainPanel.add(textField, BorderLayout.PAGE_END);

        setChatPanel();
        frame.add(mainPanel);
        frame.pack();
    }

    /**
     * calls method from client to check name availability<br>
     * sets up the group chat
     */
    public void setChatPanel() {
        writeInBoldText("Welcome to the group chat!\nWrite \"/commands\" to see all commands\n");
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

    /**
     * gets the username from client using JOptionPane<br>
     * loads last used name from file<br>
     * writes the name in a file
     *
     * @return client username
     */
    public String getName() {
        String usernameFile = "username";
        TextField nameInput = new TextField();
        nameInput.setText(read(usernameFile));
        boolean nameIncorrect;

        do {
            nameIncorrect = false;
            int option = JOptionPane.showConfirmDialog(null, nameInput, "Enter your username for the group chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
            if (option == JOptionPane.OK_OPTION) {

                clientUsername = nameInput.getText().strip();

                if (clientUsername.length() < 2) {
                    nameIncorrect = true;
                    JOptionPane.showMessageDialog(null, "Name must be at least 2 characters long", "Invalid Username", JOptionPane.ERROR_MESSAGE);
                }
                if (clientUsername.contains("\s")) {
                    nameIncorrect = true;
                    JOptionPane.showMessageDialog(null, "Name cannot include whitespace", "Invalid Username", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.exit(0);
            }
        } while (nameIncorrect);

        write(clientUsername, usernameFile);
        clientUsername = String.valueOf(clientUsername.charAt(0)).toUpperCase() + clientUsername.substring(1);
        return clientUsername;
    }

    /**
     * writes in the text and sends a Message to the server
     *
     * @param messageToSend Message to send from the client
     */
    private void sendOutMessage(String messageToSend) {
        messageToSend = messageToSend.strip();
        writeInText(messageToSend);

        Message message;
        switch (messageToSend.charAt(0)) {
            case '@' -> {
                messageToSend = messageToSend.substring(1);
                message = new Message(messageToSend.split(" ", 2)[1], Type.PRIVATE_MESSAGE, messageToSend.split(" ", 2)[0], clientUsername);
            }
            case '/' -> {
                messageToSend = messageToSend.substring(1).strip();
                message = new Message(messageToSend, Type.COMMAND, clientUsername, clientUsername);
            }
            default -> message = new Message(messageToSend, Type.MESSAGE, clientUsername);
        }
        client.sendMessages(message.toJson());
    }

    /**
     * converts message in json to Message object<br>
     * writes int the message
     *
     * @param text message in json
     */
    public void writeInMessage(String text) {
        Message message = Message.fromJson(text);
        writeInMessage(message);
    }

    /**
     * writes in a message from another client
     *
     * @param message message from another client
     */
    public void writeInMessage(Message message) {
        if (message.getType() == Type.MESSAGE) {
            writeInBoldText(message.getSentFrom() + ": ");
        } else if (message.getType() == Type.PRIVATE_MESSAGE) {
            writeInBoldText(message.getSentFrom() + " private message: ");
        }
        writeInText(message.getText());
    }

    /**
     * writes in a basic text
     *
     * @param text text to write in
     */
    private void writeInText(String text) {
        try {
            doc.insertString(doc.getLength(), text + '\n', null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes in a text in bold
     *
     * @param text text to write in bold
     */
    private void writeInBoldText(String text) {
        Style style = chat.addStyle("Style", null);
        StyleConstants.setBold(style, true);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets the ip address and port from the client using JOptionPane<br>
     * loads last used ip and port<br>
     * saves ip and port to files<br>
     *
     * @return a String[]<br>
     * [0] = ip address<br>
     * [1] = port
     */
    public String[] getIpPort() {
        String ipFile = "ipAddress", portFile = "port";

        JTextField ipAddressField = new JTextField(), portField = new JTextField();
        ipAddressField.setText(read(ipFile));
        String filePort = read(portFile);
        try {
            Integer.parseInt(filePort);
            portField.setText(filePort);
        }catch (NumberFormatException ignored){
        }


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

        boolean incorrectInput;
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

    //region fileWriteRead

    /**
     * writes text in to a file
     *
     * @param text     text to write
     * @param fileName name of the file
     */
    private void write(String text, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + ".txt"))) {
            bw.write(text);
        } catch (IOException e) {
            System.out.println("Something wrong with the file");
        }
    }

    /**
     * reads text from a file
     *
     * @param fileName file to read text from
     * @return text from file
     */
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
    //endregion

    /**
     * makes a frame that shows an error
     *
     * @param message server error message
     */
    public void serverErrorFrame(String message) {
        JFrame errorFrame = new JFrame();
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

}
