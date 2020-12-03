package client.control;

import client.view.LoginView;
import client.view.OnlineView;
import model.NguoiChoi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController extends ClientController{
    private final int MAIN_REQUEST_PORT;
    private LoginView loginView;

    public LoginController(String hostname, int hostPort , LoginView loginView){
        super(hostname);
        MAIN_REQUEST_PORT = hostPort;
        this.loginView = loginView;
        loginView.setVisible(true);
        openConnection(hostPort);
        loginView.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NguoiChoi nguoichoi = loginView.getNguoiChoiFromInputs();
                int res = checkLogin(nguoichoi);
                if (res != -1) {
                    nguoichoi.setId(res);
                    OnlineView onlineView = new OnlineView(nguoichoi);
                    loginView.setVisible(false);
                    onlineView.setVisible(true);
                    OnlineController onlineController = new OnlineController(hostname, MAIN_REQUEST_PORT, 9998, onlineView);
                    onlineController.play();
                }
                else {
                    loginView.showDialog("Thông tin đăng nhập sai");
                }
            }
        });
    }

    public int checkLogin(NguoiChoi user){
        return (int) requestSendToServer(MAIN_REQUEST_PORT, "Login", user).getObject();
    }

}
