package client.control;

import client.view.OnlineView;
import client.view.RankView;
import model.BangXepHang;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class RankController extends ClientController{

    private final int MAIN_REQUEST_PORT;
    private final RankView rankView;

    public RankController(String hostname, int port, RankView rankView) {
        super(hostname);
        this.MAIN_REQUEST_PORT = port;
        openConnection(MAIN_REQUEST_PORT);
        this.rankView = rankView;
        this.rankView.updateTable(getRankings());
        this.rankView.setVisible(true);

        this.rankView.addExitButtonListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OnlineView onlineView = new OnlineView(rankView.getNguoiChoi());
                rankView.dispose();
                onlineView.setVisible(true);
                OnlineController onlineController = new OnlineController(hostName, MAIN_REQUEST_PORT, 9998, onlineView);
                onlineController.play();
            }
        });
    }

    public ArrayList<BangXepHang> getRankings() {
        return (ArrayList<BangXepHang>) requestObjectFromServer(MAIN_REQUEST_PORT, "Rankings").getObject();
    }
}
