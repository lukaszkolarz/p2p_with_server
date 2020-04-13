package Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * The ServerSock class implements server socket and tools for using it in simple way.
 */
public class Server extends Thread implements ActionListener {

    private ServerSocket server;
    private int port;
    private String ip;
    private JFrame window;
    private JButton button, endButton, startButton;
    private JTextField portField;
    private boolean serverStarted = false;
    private InetAddress local;

    /**
     * constructor
     */
    public Server(){
        try{
            insertServerPort();
            local = InetAddress.getLocalHost();
            ip = local.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Cannot get host address");
            System.exit(-1);
        }
    }

    /**
     * creates new ServerSocket
     */
    private void newServerSocket(){
        try {
            server = new ServerSocket(port, 10, local);
            server.setReuseAddress(true);
            display();

        } catch (IOException e) {
            System.out.println("Cannot listen on port: " + port);
        } catch (SecurityException e) {
            System.out.println("This operation is forbidden");
            System.exit(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong input");
            JOptionPane.showMessageDialog(null,"Wrong port number",
                    "Warning",JOptionPane.WARNING_MESSAGE);
            insertServerPort();
        }
    }

    /**
     * Thread override function starts accepts new connection requests
     */
    @Override
    public void run(){
        serverStarted = true;
        while(true) {
            SrvThread w;
            try{
                w = new SrvThread(server.accept());
                Thread t = new Thread(w);
                t.start();
            } catch (IOException e) {
                System.out.println("Accept failed");
                System.exit(-1);
            }
        }
    }

    /**
     * GUI - inserting server port
     */
    public void insertServerPort(){
        window = new JFrame();
        window.setTitle("Insert server port");
        window.setLayout(null);
        window.setSize(210, 130);
        window.setResizable(false);

        JLabel serverPortLabel = new JLabel("Server Port:");
        serverPortLabel.setBounds(30,20,130,20);
        window.add(serverPortLabel);

        portField = new JTextField();
        portField.setBounds(110, 20, 60, 20);
        window.add(portField);

        button = new JButton("START");
        button.setBounds(50, 55, 100, 30);
        window.add(button);
        button.addActionListener(this);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    /**
     * GUI - displays server parameters, let safely exit
     */
    public synchronized void display(){
        JFrame info = new JFrame();
        info.setTitle("Server info");
        info.setLayout(null);
        info.setSize(250, 160);
        info.setResizable(false);

        JLabel serverIP = new JLabel("Server IP: " + server.getInetAddress().getHostAddress());
        serverIP.setBounds(30, 20, 180, 20);
        info.add(serverIP);

        JLabel serverPort = new JLabel("Server Port: " + server.getLocalPort());
        serverPort.setBounds(30,40,180,20);
        info.add(serverPort);

        endButton = new JButton("EXIT");
        endButton.setBounds(20, 75, 95, 30);
        info.add(endButton);
        endButton.addActionListener(this);

        startButton = new JButton("Start Server");
        startButton.setBounds(135, 75, 95, 30);
        info.add(startButton);
        startButton.addActionListener(this);

        info.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        info.setLocationRelativeTo(null);
        info.setVisible(true);
    }

    /**
     * invoke when the action occurs
     * @param e action
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            if (portField.getText().length() == 0) {
                JOptionPane.showMessageDialog(null, "Please fill the port",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    port = Integer.parseInt(portField.getText());
                    if (port >= 0 && port <= 65535) {
                        window.setVisible(false);
                        newServerSocket();
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong port number",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Wrong port number",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }else if (e.getSource() == startButton){
            if (!serverStarted) {
                this.start();
                JOptionPane.showMessageDialog(null, "Server started",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Server is running",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == endButton){
            try{
                this.interrupt();
                server.close();
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Cannot close server");
            } catch (NullPointerException ex) {
                System.out.println("Socket already closed");
                System.exit(0);
            }
        }
    }
}

