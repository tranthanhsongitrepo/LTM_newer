package client.control;

import client.view.RankView;

import java.util.ArrayList;

public class RankController {

    private final int MAIN_REQUEST_PORT;
    private RankView rankView;
    private ServerDAO serverDAO;
    
    public RankController(int port, ServerDAO serverDAO, RankView rankView) {
        this.MAIN_REQUEST_PORT = port;
        serverDAO.openConnection(MAIN_REQUEST_PORT);
        this.serverDAO = serverDAO;
        this.rankView = rankView;
        this.rankView.updateTable(getRankings());
        this.rankView.setVisible(true);
    }

    public ArrayList<Object[]> getRankings() {
        return (ArrayList<Object[]>) serverDAO.requestObjectFromServer(MAIN_REQUEST_PORT, "Rankings").getObject();
    }
}
