package client.control;

import client.view.GameView;
import model.ToaDo;

import java.awt.event.MouseAdapter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Helper class to control the game
 * When first initialized it'll start a thread that'll constantly ask for the current turn from the server and add an event listener
 * to the GameView's table that'll only allows the player to move if it's their turn
 * **/

public class GameController {
    private char turn;
    private int currentTime;
    private final GameView gameView;
    private final Object lock = new Object();
    private final int MAIN_REQUEST_PORT;
    private ServerDAO serverDAO;
    private final int timeLimit;
    private boolean running;
    TimerTask task;
    Timer timer;
    public GameController(int port, ServerDAO serverDAO, GameView gameView) {
        this.gameView = gameView;
        this.gameView.setVisible(true);
        this.serverDAO = serverDAO;
        this.MAIN_REQUEST_PORT = port;
        this.turn = 1;
        this.currentTime = 0;
        this.timeLimit = 15;
        this.running = false;
    }

    public void play() {

        char piece = (char) serverDAO.requestSendToServer(this.MAIN_REQUEST_PORT, "Piece", this.gameView.getNguoichoi()).getObject();
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
                    GameController.this.serverDAO.requestSendToServer(MAIN_REQUEST_PORT, "ChangeTurn", GameController.this.gameView.getNguoichoi());
                    cancel();
                }
            }

        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    class MoveListener extends Thread {
        public MoveListener() {
            MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (turn != gameView.getPiece()) {
                        gameView.showDialog("Hiện tại không phải lượt của bạn");
                    }
                    else {
                        ToaDo toaDo = new ToaDo(gameView.getJTable().getSelectedColumn(), gameView.getJTable().getSelectedRow());
                        if (gameView.getJTable().getValueAt(toaDo.getY(), toaDo.getX()) == null) {
                            gameView.playerMove(toaDo);
                            GameController.this.serverDAO.requestSendToServer(MAIN_REQUEST_PORT, "NuocDi", toaDo);
                            timer.cancel();
                        }
                    }
                }
            };
            gameView.getJTable().addMouseListener(mouseAdapter);
        }

        public void run() {

            boolean timerReseted = false;
            // TODO: Synchronize the time between the two client
            while (running) {

                turn = (char) GameController.this.serverDAO.requestSendToServer(MAIN_REQUEST_PORT, "Turn", GameController.this.gameView.getNguoichoi()).getObject();

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
                ToaDo nuocDiGanNhat = (ToaDo) GameController.this.serverDAO.requestSendToServer(MAIN_REQUEST_PORT, "ToaDo", GameController.this.gameView.getNguoichoi()).getObject();

                if (nuocDiGanNhat == null)
                    continue;

                if (GameController.this.gameView.getJTable().getValueAt(nuocDiGanNhat.getY(), nuocDiGanNhat.getX()) == null) {
                    GameController.this.gameView.oponentMove(nuocDiGanNhat);
                }

                int status = (int) GameController.this.serverDAO.requestObjectFromServer(MAIN_REQUEST_PORT, "Status").getObject();
                if (status != -1) {
                    if (status != 0)
                        gameView.showDialog("Người chơi " + (status == 'x' ? 1 : 2) + " thắng");
                    else
                        gameView.showDialog("Hòa");
                    return;
                }
            }
        }
    }
}
