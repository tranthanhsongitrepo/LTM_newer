package client;

import client.control.LoginController;
import client.control.ClientController;
import client.view.LoginView;

public class ClientRun {
    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController("localhost", 9999, loginView);

    }
}
