package client;

import client.control.LoginController;
import client.control.ServerDAO;
import client.view.LoginView;

public class ClientRun {
    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(9999, new ServerDAO("localhost"), loginView);

    }
}
