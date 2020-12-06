package server.control;

import model.BangXepHang;
import model.Message;
import model.NguoiChoi;
import model.ToaDo;
import server.control.DAO.NguoiChoiDAO;
import server.control.DAO.BangXepHangDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ServerController {
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
                oss.get(address).reset();
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
            System.out.println(address);
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
        sendMessage(address, message);
        return receiveMessage(address);
    }

    public ServerGameController getOngoingGame(InetAddress nguoiChoi) {
        return onGoingGames.get(nguoiChoi);
    }

    public NguoiChoi getNguoiChoiFromAddress(InetAddress address) {
        return addressNguoiChoi.get(address);
    }

    public ArrayList<BangXepHang> getOnlineList() {
        BangXepHangDAO bangXepHangDAO = new BangXepHangDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password");
        Set<NguoiChoi> onlineList = nguoiChoiAddresses.keySet();

        ArrayList<BangXepHang> rankings = bangXepHangDAO.getRankings(onlineList);

        for (BangXepHang it : rankings) {
            if (onGoingGames.get(nguoiChoiAddresses.get(it)) != null) {
                it.setTrangThai("Bận");
            }
            else {
                it.setTrangThai("Online");
            }
        }
        bangXepHangDAO.closeConnection();
        return rankings;
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

                    os.reset();

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
                        break;
                    }

                    switch (message.getAction()) {
                        case "Challenge":
                            // Get the opponent's address
                            String opponentName = (String) message.getObject();

                            InetSocketAddress opponentAddress = new InetSocketAddress(nguoiChoiAddresses.get(new NguoiChoi(opponentName, "")), 9999);
                            NguoiChoi nguoichoi1 = addressNguoiChoi.get(this.clientAddress.getAddress());
                            // Send a challenge to the second user
                            response = new Message("Challenge", nguoichoi1);
                            sendMessage(opponentAddress, response);
                            break;

                        case "Turn":
                            message.setObject(getOngoingGame(this.clientAddress.getAddress()).getLuot());
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
                            nguoiChoiDAO.closeConnection();
                            nguoiChoi.setId(res);

                            if (res != -1) {
                                nguoiChoiAddresses.put(nguoiChoi, this.clientAddress.getAddress());
                                addressNguoiChoi.put(this.clientAddress.getAddress(), nguoiChoi);
                            }
                            sendMessage(this.clientAddress, new Message("Results", res));
                            break;

                        case "Rankings":
                            BangXepHangDAO bxhDAO = new BangXepHangDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password");
                            response = new Message("Rankings", bxhDAO.getRankings());
                            bxhDAO.closeConnection();

                            sendRequestToClient(this.clientAddress, response);
                            break;

                        case "OnlineList":
                            response = message;
                            response.setObject(getOnlineList());
                            sendMessage(this.clientAddress, response);
                            break;
                        case "Accept":
                            NguoiChoi opponent = (NguoiChoi) message.getObject();

                            opponentAddress = new InetSocketAddress(nguoiChoiAddresses.get(new NguoiChoi(opponent.getTenDangNhap(), ""))
                                    , 9998);
                            sendMessage(opponentAddress, new Message("Accept"));
                            opponentAddress = new InetSocketAddress(nguoiChoiAddresses.get(new NguoiChoi(opponent.getTenDangNhap(), ""))
                                    , 9999);
                            System.out.println("Accepted " + opponentAddress);
                            ServerGameController serverGameController = new ServerGameController(getNguoiChoiFromAddress(this.clientAddress.getAddress()), opponent , 100);
                            onGoingGames.put(opponentAddress.getAddress(), serverGameController);
                            onGoingGames.put(this.clientAddress.getAddress(), serverGameController);
                            break;
                        case "Decline":
                            opponent = (NguoiChoi) message.getObject();

                            opponentAddress = new InetSocketAddress(nguoiChoiAddresses.get(new NguoiChoi(opponent.getTenDangNhap(), ""))
                                    , 9998);
                            sendMessage(opponentAddress, new Message("Decline"));
                            System.out.println("Declined " + opponentAddress);
                            break;

                        case "Piece":
                            ServerGameController game = getOngoingGame(this.clientAddress.getAddress());
                            sendMessage(this.clientAddress, new Message("Piece", game.getNguoiChoi1().equals(getNguoiChoiFromAddress(this.clientAddress.getAddress())) ? 'x' : 'o'));
                            break;
                        case "Stop":
                            sendMessage(this.clientAddress, new Message("Stop"));

                            onGoingGames.remove(this.clientAddress.getAddress());
                            return;

                        case "Quit":
                            onGoingGames.get(this.clientAddress.getAddress()).thoatGame(addressNguoiChoi.get(this.clientAddress.getAddress()).getId());
                            sendMessage(this.clientAddress, new Message("Quit"));
                            break;

                        case "Finished":
                            onGoingGames.remove(this.clientAddress.getAddress());
                            sendMessage(this.clientAddress, new Message("OK"));
                            break;

                        case "Rematch":
                            System.out.println("Rematch");;
                            game = getOngoingGame(this.clientAddress.getAddress());
                            game.rematch((Boolean) message.getObject());

                            // Wait for all 2 clients to respond
                            while (game.getTraLoi() != 2) {}

                            sendMessage(this.clientAddress, new Message("Rematch", game.getDauLai()));
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
