package client.control;

import client.view.GameView;
import client.view.OnlineView;
import model.NguoiChoi;
import model.ToaDo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Helper class to control the game
 * When first initialized it'll start a thread that'll constantly ask for the current turn from the server and add an event listener
 * to the GameView's table that'll only allows the player to move if it's their turn
 * **/

public class GameController extends ClientController{
    private char turn;
    private int currentTime;
    private GameView gameView;
    private final int MAIN_REQUEST_PORT, SUB_REQUEST_PORT;
    private final int timeLimit;
    private boolean running;
    private TimerTask task;
    private Timer timer;
    public GameController(String hostname, int mainPort, int subPort, GameView gameView) {
        super(hostname);
        this.gameView = gameView;
        this.gameView.setVisible(true);
        this.MAIN_REQUEST_PORT = mainPort;
        this.SUB_REQUEST_PORT = subPort;
        openConnection(MAIN_REQUEST_PORT);
        openConnection(SUB_REQUEST_PORT);
        this.turn = 1;
        this.currentTime = 0;
        this.timeLimit = 15;
        this.running = true;
    }

    public void play() {

        char piece = (char) requestSendToServer(this.MAIN_REQUEST_PORT, "Piece", this.gameView.getNguoichoi()).getObject();
        this.gameView.setPiece(piece);
        System.out.println(piece);
        MoveListener moveListener = new MoveListener();
        moveListener.start();
    }

    public void restartTimer() {
        currentTime = 0;
        if (timer != null) {
            timer.cancel();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                if (currentTime < timeLimit) {
                    currentTime++;
                    GameController.this.gameView.setTime(currentTime);
                }
                else {
                    GameController.this.requestSendToServer(SUB_REQUEST_PORT, "ChangeTurn", GameController.this.gameView.getNguoichoi());
                    cancel();
                }
            }

        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void addListeners() {
        MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (turn != gameView.getPiece()) {
                    gameView.showMessageDialog("Hiện tại không phải lượt của bạn");
                }
                else {
                    ToaDo toaDo = new ToaDo(gameView.getJTable().getSelectedColumn(), gameView.getJTable().getSelectedRow());
                    if (gameView.getJTable().getValueAt(toaDo.getY(), toaDo.getX()) == null) {
                        gameView.playerMove(toaDo);
                        GameController.this.requestSendToServer(SUB_REQUEST_PORT, "NuocDi", toaDo);
                        timer.cancel();
                    }
                }
            }
        };
        gameView.addQuitButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameView.showConfirmDialog("Bạn có chắc bạn muốn thoát trận?") == JOptionPane.YES_OPTION) {
                    requestObjectFromServer(MAIN_REQUEST_PORT, "Quit");
                }
            }
        });
        gameView.getJTable().addMouseListener(mouseAdapter);
    }

    class MoveListener extends Thread {
        public MoveListener() {
            addListeners();
        }

        public void run() {
            boolean rematch = false;
            boolean timerReseted = false;
            // TODO: Synchronize the time between the two client
            while (running) {

                turn = (char) GameController.this.requestSendToServer(MAIN_REQUEST_PORT, "Turn", GameController.this.gameView.getNguoichoi()).getObject();

                if (turn == GameController.this.gameView.getPiece()) {
                    if (!timerReseted) {
                        timerReseted = true;
                        restartTimer();
                        continue;
                    }
                }
                else {
                    timerReseted = false;
                }
                ToaDo nuocDiGanNhat = (ToaDo) GameController.this.requestSendToServer(MAIN_REQUEST_PORT, "ToaDo", GameController.this.gameView.getNguoichoi()).getObject();

                if (nuocDiGanNhat != null && GameController.this.gameView.getJTable().getValueAt(nuocDiGanNhat.getY(), nuocDiGanNhat.getX()) == null) {
                    GameController.this.gameView.oponentMove(nuocDiGanNhat);
                }

                int status = (int) GameController.this.requestObjectFromServer(MAIN_REQUEST_PORT, "Status").getObject();
                if (status != -1) {
                    switch (status) {
                        case 'x':
                            gameView.showMessageDialog("Người chơi 1 thắng");
                            break;
                        case 'o':
                            gameView.showMessageDialog("Người chơi 2 thắng");
                            break;
                        case -2:
                            if (GameController.this.gameView.getPiece() == 'o')
                                gameView.showMessageDialog("Đối thủ đã thoát");
                            break;
                        case -3:
                            if (GameController.this.gameView.getPiece() == 'x')
                                gameView.showMessageDialog("Đối thủ đã thoát");
                            break;
                        case 0:
                            gameView.showMessageDialog("Hòa");
                            break;
                    }


                    if (status != -2 && status != -3) {
                        if (gameView.showConfirmDialog("Bạn có muốn thách đấu lại người chơi này?") == JOptionPane.YES_OPTION) {
                            if (requestSendToServer(MAIN_REQUEST_PORT, "Rematch", true).getObject().equals(true)) {
                                rematch = true;
                            } else {
                                gameView.showMessageDialog("Người chơi còn lại từ chối");
                            }
                        } else {
                            requestSendToServer(MAIN_REQUEST_PORT, "Rematch", false);
                        }
                    }

                    running = false;

                    if (rematch) {
                        NguoiChoi nguoiChoi = gameView.getNguoichoi();
                        gameView.dispose();

                        GameView newGameView = new GameView(nguoiChoi);
                        GameController newGameController = new GameController(hostName, MAIN_REQUEST_PORT, SUB_REQUEST_PORT, newGameView);
                        newGameController.play();
                    } else {

                        OnlineView onlineView = new OnlineView(GameController.this.gameView.getNguoichoi());
                        onlineView.setVisible(true);
                        OnlineController onlineController = new OnlineController(hostName, MAIN_REQUEST_PORT, SUB_REQUEST_PORT, onlineView);
                        onlineController.play();
                        gameView.dispose();
                    }
                }
            }

            if (!rematch)
                GameController.this.requestObjectFromServer(MAIN_REQUEST_PORT, "Finished");

        }
    }
}
