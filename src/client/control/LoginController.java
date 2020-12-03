package client.control;

import client.view.LoginView;
import client.view.OnlineView;
import model.NguoiChoi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private final int MAIN_REQUEST_PORT;
    private LoginView loginView;
    private ServerDAO serverDAO;
    
    public LoginController(int hostPort, ServerDAO serverDAO, LoginView loginView){
        MAIN_REQUEST_PORT = hostPort;
        this.loginView = loginView;
        this.serverDAO = serverDAO;
        loginView.setVisible(true);
        serverDAO.openConnection(hostPort);
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
                    serverDAO.closeConnection(MAIN_REQUEST_PORT);
                    OnlineController onlineController = new OnlineController(MAIN_REQUEST_PORT, 9998, serverDAO, onlineView);
                    onlineController.play();
                }
                else {
                    loginView.showDialog("Thông tin đăng nhập sai");
                }
            }
        });
    }

    public int checkLogin(NguoiChoi user){
        return (int) serverDAO.requestSendToServer(MAIN_REQUEST_PORT, "Login", user).getObject();
    }

}
