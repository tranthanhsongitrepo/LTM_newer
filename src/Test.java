import server.control.ServerController;

public class Test {
    public static void main(String[] args) {
        ServerController gameController = new ServerController();
        gameController.openConnection(9999);
        gameController.openConnection(9998);
    }
}
