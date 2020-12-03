package server.control;

import model.Message;
import model.NguoiChoi;
import model.ToaDo;
import server.DAO.NguoiChoiDAO;
import server.DAO.RankingDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ServerController extends Thread{
    private HashMap<Integer, ServerSocket> serverSockets;
    protected HashMap<InetSocketAddress, ObjectInputStream> iss;
    protected HashMap<InetSocketAddress, ObjectOutputStream> oss;
    protected HashMap<InetAddress, ServerGameController> onGoingGames;
    protected HashMap<NguoiChoi, InetAddress> nguoiChoiAddresses;
    protected HashMap<InetAddress, NguoiChoi> addressNguoiChoi;
    public ServerController() {
        serverSockets = new HashMap<>();
        iss = new HashMap<>();
        oss = new HashMap<>();
        this.nguoiChoiAddresses = new HashMap<>();
        this.onGoingGames = new HashMap<>();
        this.addressNguoiChoi = new HashMap<>();
    }

    public void openConnection(int port) {
        try {
            this.serverSockets.put(port, new ServerSocket(port));
            ConnectionListener connectionListener = new ConnectionListener(port);
            connectionListener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(InetSocketAddress address, Message message) {
        try {
            synchronized (oss.get(address)) {
                oss.get(address).writeObject(message);
                oss.get(address).flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message receiveMessage(InetSocketAddress address) {
        try {
            synchronized (iss.get(address)) {
                return (Message) iss.get(address).readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void closeConnection(InetSocketAddress address){
        try {
            oss.get(address).close();
            iss.get(address).close();
            iss.remove(address);
            oss.remove(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message sendRequestToClient(InetSocketAddress address, Message message) {
        System.out.println(message.getAction());
        sendMessage(address, message);
        return receiveMessage(address);
    }

    public ServerGameController getOngoingGame(InetAddress nguoiChoi) {
        return onGoingGames.get(nguoiChoi);
    }

    public NguoiChoi getNguoiChoiFromAddress(InetAddress address) {
        return addressNguoiChoi.get(address);
    }


    class ConnectionListener extends Thread {
        private final int port;

        public ConnectionListener(int port) {
            this.port = port;
        }

        public void run() {
            while (true) {
                Socket clientSocket;
                try {
                    clientSocket = serverSockets.get(port).accept();
                    InetSocketAddress address = new InetSocketAddress(clientSocket.getInetAddress(), clientSocket.getLocalPort());
                    System.out.println("Client " + clientSocket.getInetAddress() + " accepted");
                    ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());

                    iss.put(address, is);
                    oss.put(address, os);

                    System.out.println(iss.size());
                    RequestListener requestListener = new RequestListener(address);
                    requestListener.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class RequestListener extends Thread {
        private final InetSocketAddress clientAddress;
        private boolean running;

        public RequestListener(InetSocketAddress clientAddress) {
            this.clientAddress = clientAddress;
            this.running = true;
        }

        public void run() {
            while (running) {

                try {
                    Message message = receiveMessage(clientAddress), response;
                    if (message == null) {
                        closeConnection(this.clientAddress);
                        break;
                    }
                    System.out.println(message.getAction() + " " + clientAddress);

                    switch (message.getAction()) {
                        case "Challenge":
                            // Get the opponent's address
                            String opponentName = (String) message.getObject();

                            InetSocketAddress opponentAddress = new InetSocketAddress(nguoiChoiAddresses.get(new NguoiChoi(opponentName, "")), this.clientAddress.getPort());
                            NguoiChoi nguoichoi1 = addressNguoiChoi.get(this.clientAddress.getAddress());
                            // Send a challenge to the second user
                            response = new Message("Challenge", nguoichoi1);
                            sendMessage(opponentAddress, response);
                            break;

                        case "Turn":
                            message.setObject(getOngoingGame(this.clientAddress.getAddress()).getTurn());
                            sendMessage(clientAddress, message);
                            break;
                        case "ToaDo":
                            message.setObject(getOngoingGame(this.clientAddress.getAddress()).getNuocDiGanNhat());
                            sendMessage(clientAddress, message);
                            break;
                        case "NuocDi":
                            getOngoingGame(this.clientAddress.getAddress()).setNuocDiGanNhat((ToaDo) message.getObject());
                            getOngoingGame(this.clientAddress.getAddress()).move();
                            response = new Message("OK", null);
                            sendMessage(clientAddress, response);
                            break;
                        case "Status":
                            response = new Message("OK", getOngoingGame(this.clientAddress.getAddress()).checkWin());
                            sendMessage(clientAddress, response);
                            break;

                        case "ChangeTurn":
                            getOngoingGame(this.clientAddress.getAddress()).changeTurn();
                            response = new Message("OK", null);
                            sendMessage(clientAddress, response);
                            break;

                        case "Login":
                            NguoiChoiDAO nguoiChoiDAO = new NguoiChoiDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password");

                            NguoiChoi nguoiChoi = (NguoiChoi) message.getObject();
                            int res = nguoiChoiDAO.checkLogin(nguoiChoi);
                            nguoiChoi.setId(res);

                            if (res != -1) {
                                nguoiChoiAddresses.put(nguoiChoi, this.clientAddress.getAddress());
                                addressNguoiChoi.put(this.clientAddress.getAddress(), nguoiChoi);
                            }
                            sendMessage(this.clientAddress, new Message("Results", res));
                            break;

                        case "Rankings":
                            response = new Message("Rankings", new RankingDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password").getRankings());
                            sendRequestToClient(this.clientAddress, response);
                            break;

                        case "OnlineList":
                            response = message;
                            Set<NguoiChoi> onlineList = nguoiChoiAddresses.keySet();

                            for (NguoiChoi it : onlineList) {
                                if (onGoingGames.get(nguoiChoiAddresses.get(it)) != null) {
                                    it.setTrangThai("Bận");
                                }
                            }

                            response.setObject(onlineList.toArray());
                            sendMessage(this.clientAddress, response);
                            break;
                        case "Accept":
                            NguoiChoi opponent = (NguoiChoi) message.getObject();

                            opponentAddress = new InetSocketAddress(nguoiChoiAddresses.get(new NguoiChoi(opponent.getTenDangNhap(), ""))
                                    , this.clientAddress.getPort());
                            sendMessage(opponentAddress, new Message("Accept"));
                            System.out.println("Accepted " + opponentAddress);
                            ServerGameController serverGameController = new ServerGameController(opponent, getNguoiChoiFromAddress(this.clientAddress.getAddress()), 100);
                            onGoingGames.put(opponentAddress.getAddress(), serverGameController);
                            onGoingGames.put(this.clientAddress.getAddress(), serverGameController);
                            break;
                        case "Decline":
                            break;

                        case "Piece":
                            ServerGameController game = getOngoingGame(this.clientAddress.getAddress());
                            sendMessage(this.clientAddress, new Message("Piece", game.getNguoiChoi1().equals(getNguoiChoiFromAddress(this.clientAddress.getAddress())) ? 'x' : 'o'));
                            break;

                    }
                }
                catch (Exception e) {
                    break;
                }
            }
            closeConnection(this.clientAddress);
        }
    }
}
