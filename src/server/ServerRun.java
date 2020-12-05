package server;

import server.control.ServerController;

public class ServerRun {
    public static void main(String[] args) {
        ServerController gameController = new ServerController();
        gameController.openConnection(9999);
        gameController.openConnection(9998);
    }
}
