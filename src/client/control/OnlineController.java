package client.control;

import client.view.GameView;
import client.view.OnlineView;
import client.view.RankView;
import model.Message;
import model.NguoiChoi;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OnlineController {
    private OnlineView onlineView;
    private final int SUB_REQUEST_PORT;
    private final int MAIN_REQUEST_PORT;
    private ServerDAO serverDAO;
    private volatile boolean running;

    public OnlineController( int mainPort, int subPort, ServerDAO serverDAO, OnlineView onlineView) {
        this.serverDAO = serverDAO;
        this.MAIN_REQUEST_PORT = mainPort;
        this.SUB_REQUEST_PORT = subPort;
        this.onlineView = onlineView;

        serverDAO.openConnection(SUB_REQUEST_PORT);

        onlineView.addRankingButtonListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RankView rankView = new RankView(onlineView.getNguoichoi());
                RankController rankController = new RankController(MAIN_REQUEST_PORT, serverDAO, rankView);
                rankView.setVisible(true);
                onlineView.dispose();

            }
        });
        onlineView.addRefreshButtonListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Object[] objects = getAvailablePlayers(SUB_REQUEST_PORT);
                if (objects != null)
                    onlineView.updateTable(objects);
            }
        });

        onlineView.addOnlinePlayersTableListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onlineView.showConfirmDialog("Bạn có muốn thách đấu người chơi này") == JOptionPane.YES_OPTION){
                        if (serverDAO.requestSendToServer(MAIN_REQUEST_PORT,"Challenge", onlineView.getJTable().getValueAt(onlineView.getJTable().getSelectedRow(), 0)).getAction().equals("Accept")) {

                            // Lock the thread until the RequestListener is fully closed
                            while (true) {
                                if (!running)
                                    break;
                            }
                            GameView gameView = new GameView(onlineView.getNguoichoi());
                            GameController gameController = new GameController(MAIN_REQUEST_PORT, serverDAO, gameView);
                            gameController.play();
                            onlineView.dispose();
                    }
                }
         }
        });
    }

    public Object[] getAvailablePlayers(int port) {
        return (Object[]) serverDAO.requestObjectFromServer(port, "OnlineList").getObject();
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
                    Message message = (Message) serverDAO.receiveObject(MAIN_REQUEST_PORT);
                    switch (message.getAction()) {
                        case "Challenge":
                            int choice = onlineView.showConfirmDialog("Nguoi choi " + ((NguoiChoi) message.getObject()).getTenDangNhap() + " thach dau");
                            serverDAO.sendObject(MAIN_REQUEST_PORT, new Message(choice == JOptionPane.YES_OPTION ? "Accept" : "Decline", message.getObject()));
                            if (choice == JOptionPane.YES_OPTION) {
                                running = false;

                                GameView gameView = new GameView(onlineView.getNguoichoi());
                                GameController gameController = new GameController(MAIN_REQUEST_PORT, serverDAO, gameView);
                                gameController.play();
                                OnlineController.this.onlineView.dispose();
                            }
                            break;
                        case "NguoiChoi":
                            serverDAO.sendObject(MAIN_REQUEST_PORT, new Message("NguoiChoi", OnlineController.this.onlineView.getNguoichoi()));
                            break;
                        // TODO : Clean this up
                        case "Stop":
                            System.out.println("Stop");
                            running = false;
                            break;
                    }
                }
                finally {
                    break;
                }
            }
            serverDAO.closeConnection(MAIN_REQUEST_PORT);
        }
    }
}
