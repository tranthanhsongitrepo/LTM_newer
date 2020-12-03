package client.control;

import model.Message;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class ServerDAO {
    protected HashMap<Integer, Socket> clientSockets;
    protected String hostName;
    protected HashMap<Integer, ObjectOutputStream> oss;
    protected HashMap<Integer, ObjectInputStream> iss;

    public ServerDAO(String hostName) {
        this.hostName = hostName;
        this.clientSockets = new HashMap<>();
        this.oss = new HashMap<>();
        this.iss = new HashMap<>();
    }

    public synchronized Message requestSendToServer(int port, String action, Object object) {
        Message message = new Message(action, object);
        sendObject(port, message);
        return (Message) receiveObject(port);
    }

    public Object receiveObject(int port) {
        try {

            synchronized (iss.get(port)) {
                return iss.get(port).readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized Message requestObjectFromServer(int port, String action) {
        System.out.println(action);
        Message message = new Message(action);
        sendObject(port, message);
        return (Message) receiveObject(port);
    }

    public void openConnection(int port) {
        try {
            Socket socket = new Socket(hostName, port);
            this.clientSockets.put(port, socket);
            this.oss.put(port, new ObjectOutputStream(socket.getOutputStream()));
            this.iss.put(port, new ObjectInputStream(socket.getInputStream()));
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

    public void sendObject(int port, Object object){
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

    public synchronized Date getNTPTime() {
        Date time;
        try {
            String TIME_SERVER = "time-a.nist.gov";
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            long returnTime = timeInfo.getReturnTime();
            time = new Date(returnTime);
            System.out.println("Time from " + TIME_SERVER + ": " + time);
            return time;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
