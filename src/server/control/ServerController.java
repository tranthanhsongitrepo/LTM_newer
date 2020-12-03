package server.control;

import model.Message;
import model.NguoiChoi;
import model.ToaDo;
import server.DAO.NguoiChoiDAO;
import server.DAO.RankingDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    protected HashMap<NguoiChoi, ServerGameController> onGoingGames;
    protected HashMap<NguoiChoi, InetSocketAddress> nguoiChoiAddresses;

    public ServerController() {
        serverSockets = new HashMap<>();
        iss = new HashMap<>();
        oss = new HashMap<>();
        this.nguoiChoiAddresses = new HashMap<>();
        this.onGoingGames = new HashMap<>();
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

    public ServerGameController getOngoingGame(NguoiChoi nguoiChoi) {
        return onGoingGames.get(nguoiChoi);
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
        private NguoiChoi nguoiChoi;

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

                            InetSocketAddress opponentAddress = nguoiChoiAddresses.get(new NguoiChoi(opponentName, ""));
                            NguoiChoi nguoichoi1 = (NguoiChoi) sendRequestToClient(this.clientAddress, new Message("NguoiChoi", null)).getObject();
                            // Send a challenge to the second user
                            response = new Message("Challenge", nguoichoi1);
                            sendMessage(opponentAddress, response);
                            break;

                        case "Turn":
                            message.setObject(getOngoingGame(this.nguoiChoi).getTurn());
                            sendMessage(clientAddress, message);
                            break;
                        case "ToaDo":
                            message.setObject(getOngoingGame((NguoiChoi) message.getObject()).getNuocDiGanNhat());
                            sendMessage(clientAddress, message);
                            break;
                        case "NuocDi":
                            getOngoingGame(this.nguoiChoi).setNuocDiGanNhat((ToaDo) message.getObject());
                            getOngoingGame(this.nguoiChoi).move();
                            response = new Message("OK", null);
                            sendMessage(clientAddress, response);
                            break;
                        case "Status":
                            response = new Message("OK", getOngoingGame(this.nguoiChoi).checkWin());
                            sendMessage(clientAddress, response);
                            break;

                        case "ChangeTurn":
                            getOngoingGame((NguoiChoi) message.getObject()).changeTurn();
                            response = new Message("OK", null);
                            sendMessage(clientAddress, response);
                            break;

                        case "Login":
                            NguoiChoiDAO nguoiChoiDAO = new NguoiChoiDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password");
                            this.nguoiChoi = (NguoiChoi) message.getObject();
                            int res = nguoiChoiDAO.checkLogin(nguoiChoi);
                            nguoiChoi.setId(res);

                            if (res != -1)
                                nguoiChoiAddresses.put(nguoiChoi, this.clientAddress);
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
                                if (onGoingGames.containsKey(it)) {
                                    it.setTrangThai("Bận");
                                }
                            }

                            response.setObject(onlineList.toArray());
                            sendMessage(this.clientAddress, response);
                            break;
                        case "Accept":
                            NguoiChoi opponent = (NguoiChoi) message.getObject();
                            opponentAddress = nguoiChoiAddresses.get(new NguoiChoi(opponent.getTenDangNhap(), ""));
                            sendMessage(opponentAddress, new Message("Accept"));
                            sendMessage(opponentAddress, new Message("Stop"));
                            ServerGameController serverGameController = new ServerGameController(opponent, this.nguoiChoi, 100);
                            onGoingGames.put(opponent, serverGameController);
                            onGoingGames.put(this.nguoiChoi, serverGameController);
                            break;
                        case "Decline":
                            break;

                        case "Piece":
                            ServerGameController game = onGoingGames.get((NguoiChoi) message.getObject());
                            sendMessage(this.clientAddress, new Message("Piece", game.getNguoiChoi1().equals(this.nguoiChoi) ? 'x' : 'o'));
                            break;

                    }
                }
                finally {
                    break;
                }
            }
        }
    }
}
