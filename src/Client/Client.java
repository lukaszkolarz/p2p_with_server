package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Client is a class to managing connection with server ant choosing perr to connect with.
 * Client manage also connection p2p.
 */
public class Client implements ActionListener {

    private String name, opponentName;
    private String serverAddress;
    private int serverPort;
    private Integer hostsNamesAmount;
    private CliSocket client, clientClientSocket;
    private JButton connectButton, insertNameButton, refreshButton, connectHostButton;
    private JFrame connectionWindow, insertNameWindow, chooseHostWindow;
    private JTextField serverAddressInput, serverPortInput, nameInput;
    private DefaultListModel model;
    private JList list;
    private ListeningClient listener;
    private ClientServerSocket clientServerSocket;
    private String  clientServerPort = "8123";
    private boolean isServer, isReady=true;

    /**
     * constructor
     */
    public Client(){
        displayInitWindow();
    }

    /**
     * refreshes list of users connected with server in the moment
     */
    private void updateHostNames(){
        client.sendString("refresh");
        hostsNamesAmount = Integer.parseInt(listener.getMessage());
        model.removeAllElements();
        for (int i=0; i<hostsNamesAmount; i++){
            String temp = listener.getMessage();
            if (!temp.equals(name)) {
                model.addElement(temp);
            }
        }
    }

    /**
     * GUI - inserting connection data
     */
    private void displayInitWindow(){
        connectionWindow = new JFrame();                                            //define connection window
        connectionWindow.setTitle("Connection with server");
        connectionWindow.setLayout(null);
        connectionWindow.setSize(300,130);
        connectionWindow.setResizable(false);

        JLabel serverAddressLabel = new JLabel("Server address:");             //add IP label
        serverAddressLabel.setBounds(20,20,130,20);              //older reshape()
        connectionWindow.add(serverAddressLabel);

        serverAddressInput = new JTextField("");                                    //add IP textfield
        serverAddressInput.setBounds(120,20,155,20);
        connectionWindow.add(serverAddressInput);


        JLabel serverPortLabel = new JLabel("Server port:");                   //add port label
        serverPortLabel.setBounds(20,60,130,20);
        connectionWindow.add(serverPortLabel);

        serverPortInput = new JTextField("");                                       //add port textfield
        serverPortInput.setBounds(95,60,60,20);
        connectionWindow.add(serverPortInput);

        connectButton = new JButton("Connect");                                //add connect button
        connectButton.setBounds(170,55,100,30);
        connectionWindow.add(connectButton);
        connectButton.addActionListener(this);                                   //set button handler

        connectionWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            //display connection window
        connectionWindow.setLocationRelativeTo(null);
        connectionWindow.setVisible(true);
    }

    /**
     * GUI - inserting username
     */
    private void insertName(){
        insertNameWindow = new JFrame();
        insertNameWindow.setTitle("Connection with server");
        insertNameWindow.setLayout(null);
        insertNameWindow.setSize(300, 130);
        insertNameWindow.setResizable(false);

        JLabel nameLabel = new JLabel("Insert your name:");
        nameLabel.setBounds(20, 20, 130, 20);
        insertNameWindow.add(nameLabel);

        nameInput = new JTextField("");
        nameInput.setBounds(130, 20, 155, 20);
        insertNameWindow.add(nameInput);

        insertNameButton = new JButton("OK");
        insertNameButton.setBounds(100, 55, 100, 30);
        insertNameWindow.add(insertNameButton);
        insertNameButton.addActionListener(this);

        insertNameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        insertNameWindow.setLocationRelativeTo(null);
        insertNameWindow.setVisible(true);
    }

    /**
     * GUI - choosing right host
     */
    public void chooseHost(){

        chooseHostWindow = new JFrame();
        chooseHostWindow.setTitle("Choosing host");
        chooseHostWindow.setLayout(null);
        chooseHostWindow.setSize(250, 320);
        chooseHostWindow.setResizable(false);

        JLabel hostListLabel = new JLabel("Available hosts:");
        hostListLabel.setBounds(20, 15, 200, 20);
        chooseHostWindow.add(hostListLabel);

        model = new DefaultListModel();
        list = new JList(model);

        updateHostNames();

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setBounds(20, 40, 200, 200);
        chooseHostWindow.add(listScroller);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(20, 240, 100, 30);
        chooseHostWindow.add(refreshButton);
        refreshButton.addActionListener(this);

        connectHostButton = new JButton("Connect");
        connectHostButton.setBounds(130, 240, 100, 30);
        chooseHostWindow.add(connectHostButton);
        connectHostButton.addActionListener(this);

        chooseHostWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chooseHostWindow.setLocationRelativeTo(null);
        chooseHostWindow.setVisible(true);
    }

    /**
     * send a message with p2p connection request to server
     * @param opponentName concrete user, who is asked for connection
     */
    private void assignConnection(String opponentName){
        client.sendString("connect");
        client.sendString(opponentName);
        String message = "";
        message = listener.getMessage();
        if (message.equals("invitation accepted")) {
            String ip = listener.getMessage();
            String port = listener.getMessage();
            clientClientSocket = new CliSocket(ip, Integer.parseInt(port));
            chooseHostWindow.setVisible(false);
            initSocket();
        } else if (message.equals("invitation refused")){
            JOptionPane.showMessageDialog(null, "Connection refused by the user",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            list.clearSelection();
            updateHostNames();
        }
    }

    /**
     * invoke when the action occurs
     * @param e action
     */
    @Override
    public void actionPerformed(ActionEvent e) {                                    //buton handler
        if (e.getSource() == connectButton) {
            if (serverAddressInput.getText().length() == 0 || serverPortInput.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields",
                        "WARNING", JOptionPane.WARNING_MESSAGE);
            } else {
                serverAddress = serverAddressInput.getText().replaceAll(" ", "");
                serverPort = Integer.parseInt(serverPortInput.getText().replaceAll(" ", ""));

                client = new CliSocket(serverAddress, serverPort);
                client.newClientSocket();
                connectionWindow.setVisible(false);
                insertName();
            }
        }
        else if (e.getSource() == insertNameButton){
            if (nameInput.getText().length() == 0){
                JOptionPane.showMessageDialog(null, "Please insert your name",
                        "WARNING", JOptionPane.WARNING_MESSAGE);
            } else {
                name = nameInput.getText().replaceAll(" ", "");
                client.sendString(name);
                insertNameWindow.setVisible(false);
                listener = new ListeningClient(client.getIn(), this);
                listener.start();
                chooseHost();
            }
        } else if (e.getSource() == refreshButton){
            updateHostNames();
        } else if (e.getSource() == connectHostButton){
            assignConnection(list.getSelectedValue().toString());
        }
    }

    /**
     * accepts asking for connection
     * @param name peer username
     */
    public void accept(String name){
        opponentName = name;
        client.sendString("accept");
        this.clientServerSocket = new ClientServerSocket(clientServerPort);
        clientServerSocket.acceptConnection();
        chooseHostWindow.setVisible(false);
        initSocket();
    }

    /**
     * refuses asking for connection
     * @param name peer username
     */
    public void refuse(String name){
        opponentName = name;
        client.sendString("refuse");
        list.clearSelection();
    }

    /**
     * initializes p2p socket
     * depends on situation it will be CliSocket or ClientServerSocket
     */
    public synchronized void initSocket(){
        if (clientClientSocket == null){
            clientServerSocket.initSocket();
            isServer = true;
            isReady = false;
            notify();
            writeToHost("connected with other host");
            System.out.println(receiveFromHost());
        } else if (clientServerSocket == null){
            clientClientSocket.newClientSocket();
            isServer = false;
            isReady = false;
            notify();
            System.out.println(receiveFromHost());
            writeToHost("connected with other host");
        }   else {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect with host",
                    "OK",JOptionPane.INFORMATION_MESSAGE);
            System.exit(-1);
        }
    }

    /**
     * sends String to peer
     * @param data String which will be write
     */
    public void writeToHost(String data){
        waiting();
        if (isServer){
            clientServerSocket.sendString(data);
        }else{
            clientClientSocket.sendString(data);
        }
    }

    /**
     * receiving String from per
     * @return received string
     */
    public String receiveFromHost(){
        waiting();
        if (isServer){
            return clientServerSocket.receiveString();
        }else{
            return clientClientSocket.receiveString();
        }
    }

    /**
     * lets receiveFromHost and sendToHost wait for initialize p2p connection
     */
    private synchronized void waiting() {
        while (isReady) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
