package client.control;

import client.view.GameView;
import client.view.OnlineView;
import client.view.RankView;
import model.BangXepHang;
import model.Message;
import model.NguoiChoi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class OnlineController extends ClientController{
    private final OnlineView onlineView;
    private final int SUB_REQUEST_PORT;
    private final int MAIN_REQUEST_PORT;
    private volatile boolean running;

    public OnlineController(String hostname, int mainPort, int subPort, OnlineView onlineView) {
        super(hostname);
        this.MAIN_REQUEST_PORT = mainPort;
        this.SUB_REQUEST_PORT = subPort;
        this.onlineView = onlineView;

        openConnection(MAIN_REQUEST_PORT);
        openConnection(SUB_REQUEST_PORT);

        onlineView.addRankingButtonListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(MAIN_REQUEST_PORT, new Message("Stop"));
                sendMessage(SUB_REQUEST_PORT, new Message("Stop"));

                RankView rankView = new RankView(onlineView.getNguoichoi());
                RankController rankController = new RankController(hostname, MAIN_REQUEST_PORT, rankView);
                rankView.setVisible(true);
                onlineView.dispose();

            }
        });
        onlineView.addRefreshButtonListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ArrayList<BangXepHang> objects = getAvailablePlayers(SUB_REQUEST_PORT);
                if (objects != null)
                    onlineView.updateTable(objects);
            }
        });

        onlineView.addExitButtonListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
                onlineView.dispose();
            }
        });

        onlineView.addOnlinePlayersTableListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onlineView.showConfirmDialog("Bạn có muốn thách đấu người chơi này") == JOptionPane.YES_OPTION){
                        if (requestSendToServer(SUB_REQUEST_PORT,"Challenge", onlineView.getJTable().getValueAt(onlineView.getJTable().getSelectedRow(), 0)).getAction().equals("Accept")) {

                            sendMessage(MAIN_REQUEST_PORT, new Message("Stop"));
                            sendMessage(SUB_REQUEST_PORT, new Message("Stop"));

                            System.out.println("Challenge accepted");
                            // Lock the thread until the RequestListener is fully closed
                            while (true) {
                                if (!running)
                                    break;
                            }
                            GameView gameView = new GameView(onlineView.getNguoichoi());
                            GameController gameController = new GameController(hostname, MAIN_REQUEST_PORT, SUB_REQUEST_PORT, gameView);
                            gameController.play();
                            onlineView.dispose();
                    }
                        else {
                            onlineView.showMessageDialog("Đối thủ từ chối");
                        }
                }
         }
        });
    }

    public ArrayList<BangXepHang> getAvailablePlayers(int port) {
        return (ArrayList<BangXepHang>) requestObjectFromServer(port, "OnlineList").getObject();
    }

    public void play() {
        RequestListener request = new RequestListener();
        request.start();
    }

    class RequestListener extends Thread{

        public RequestListener() {
            running = true;
        }
        @Override
        public void run() {
            while (running) {
                try {
                    Message message = receiveMessage(MAIN_REQUEST_PORT);
                    switch (message.getAction()) {
                        case "Challenge":
                            int choice = onlineView.showConfirmDialog("Người chơi " + ((NguoiChoi) message.getObject()).getTenDangNhap() + " thách đấu");
                            sendMessage(MAIN_REQUEST_PORT, new Message(choice == JOptionPane.YES_OPTION ? "Accept" : "Decline", message.getObject()));
                            if (choice == JOptionPane.YES_OPTION) {
                                running = false;
                                sendMessage(MAIN_REQUEST_PORT, new Message("Stop"));
                                sendMessage(SUB_REQUEST_PORT, new Message("Stop"));
                                GameView gameView = new GameView(onlineView.getNguoichoi());
                                GameController gameController = new GameController(OnlineController.this.hostName, MAIN_REQUEST_PORT, SUB_REQUEST_PORT, gameView);
                                gameController.play();
                                OnlineController.this.onlineView.dispose();
                            }
                            break;
                        case "NguoiChoi":
                            sendMessage(MAIN_REQUEST_PORT, new Message("NguoiChoi", OnlineController.this.onlineView.getNguoichoi()));
                            break;
                        // TODO : Clean this up
                        case "Stop":
                            System.out.println("Stop");
                            running = false;
                            break;
                    }
                }
                catch (Exception e) {
                    break;
                }
            }
            OnlineController.this.sendMessage(MAIN_REQUEST_PORT, new Message("Stop"));
        }
    }
}
