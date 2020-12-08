package client.control;

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ClientController {
    protected HashMap<Integer, Socket> clientSockets;
    protected String hostName;
    protected HashMap<Integer, ObjectOutputStream> oss;
    protected HashMap<Integer, ObjectInputStream> iss;

    public ClientController(String hostName) {
        this.hostName = hostName;
        this.clientSockets = new HashMap<>();
        this.oss = new HashMap<>();
        this.iss = new HashMap<>();
    }

    public Message requestSendToServer(int port, String action, Object object) {
        System.out.println(action);
        Message message = new Message(action, object);
        sendMessage(port, message);
        return receiveMessage(port);
    }

    public Message receiveMessage(int port) {
        try {
            synchronized (iss.get(port)) {
                return (Message) iss.get(port).readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message requestObjectFromServer(int port, String action) {
        System.out.println(action);
        Message message = new Message(action);
        sendMessage(port, message);
        return receiveMessage(port);
    }

    public void openConnection(int port) {
        try {
            Socket socket = new Socket(hostName, port);
            this.clientSockets.put(port, socket);
            this.oss.put(port, new ObjectOutputStream(socket.getOutputStream()));
            this.iss.put(port, new ObjectInputStream(socket.getInputStream()));

            this.oss.get(port).reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(int port) {
        try {
            clientSockets.get(port).close();
            iss.get(port).close();
            oss.get(port).close();

            oss.remove(port);
            iss.remove(port);
            clientSockets.remove(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int port, Message object){
        try {
            synchronized (oss.get(port)) {
                oss.get(port).writeObject(object);
                oss.get(port).flush();
                oss.get(port).reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
