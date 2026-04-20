package com.devops.puissance4.client;

import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
<<<<<<< HEAD
=======
    private final String address;
    private final int port;
>>>>>>> origin/gameplay
    private Socket socket;
    private ObjectOutputStream out;
    private ClientPanel view;

<<<<<<< HEAD
    public Client(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());

        new Thread(new ClientReceive(this, socket)).start();
//        new Thread(new ClientSend(socket, out)).start();
=======
    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        if (isConnected()) {
            return;
        }

        this.socket = new Socket(address, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        new Thread(new ClientReceive(this, socket)).start();
    }

    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
>>>>>>> origin/gameplay
    }

    public void sendMessage(Message mess) {
        try {
<<<<<<< HEAD
=======
            if (!isConnected() || out == null) {
                return;
            }
>>>>>>> origin/gameplay
            out.writeObject(mess);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    public void authenticate(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        sendMessage(new Message(username, "AUTH:" + username));
    }

=======
>>>>>>> origin/gameplay
    public void messageReceived(Message mess) {
        if (view != null) view.printNewMessage(mess);
        else System.out.println(mess);
    }

<<<<<<< HEAD
    public void disconnectedServer() {
        try {
            if (out != null) out.close();
            socket.close();
            System.out.println("Déconnecté du serveur.");
            System.exit(0);
        } catch (IOException e) { e.printStackTrace(); }
    }

=======
    public synchronized void disconnectedServer() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
            out = null;
            socket = null;
            System.out.println("Deconnecte du serveur.");
            if (view != null) {
                view.printNewMessage(new Message("Serveur", "STATE:SERVER_DISCONNECTED"));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void disconnect() {
        disconnectedServer();
    }

>>>>>>> origin/gameplay
    public void setView(ClientPanel view) {
        this.view = view;
    }
}