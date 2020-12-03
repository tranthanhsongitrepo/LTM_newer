package client.control;

import client.view.RankView;

import java.util.ArrayList;

public class RankController extends ClientController{

    private final int MAIN_REQUEST_PORT;
    private RankView rankView;

    public RankController(String hostname, int port, RankView rankView) {
        super(hostname);
        this.MAIN_REQUEST_PORT = port;
        openConnection(MAIN_REQUEST_PORT);
        this.rankView = rankView;
        this.rankView.updateTable(getRankings());
        this.rankView.setVisible(true);
    }

    public ArrayList<Object[]> getRankings() {
        return (ArrayList<Object[]>) requestObjectFromServer(MAIN_REQUEST_PORT, "Rankings").getObject();
    }
}
