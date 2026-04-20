package com.devops.puissance4.client;

import com.devops.puissance4.common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private final String address;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ClientPanel view;

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
    }

    public void sendMessage(Message mess) {
        try {
            if (!isConnected() || out == null) {
                return;
            }
            out.writeObject(mess);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageReceived(Message mess) {
        if (view != null) {
            view.printNewMessage(mess);
        } else {
            System.out.println(mess);
        }
    }

    public synchronized void disconnectedServer() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            out = null;
            socket = null;
            System.out.println("Deconnecte du serveur.");
            if (view != null) {
                view.printNewMessage(new Message("Serveur", "STATE:SERVER_DISCONNECTED"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void disconnect() {
        disconnectedServer();
    }

    public void setView(ClientPanel view) {
        this.view = view;
    }
}