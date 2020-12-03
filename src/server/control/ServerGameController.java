package server.control;

import model.Message;
import model.NguoiChoi;
import model.ToaDo;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGameController {
    private int[][] banCo;
    private static final int L = 0, R = 1, U = 2, D = 3, UL = 4, UR = 5, DL = 6, DR = 7;
    private char turn;
    private ToaDo nuocDiGanNhat;
    private int currentTime;
    private int timeLimit;
    private NguoiChoi nguoiChoi1, nguoiChoi2;

    public ServerGameController(NguoiChoi nguoiChoi1, NguoiChoi nguoiChoi2, int kichCo){
        this.banCo = new int[kichCo][kichCo];
        this.nuocDiGanNhat = null;
        this.nguoiChoi1 = nguoiChoi1;
        this.nguoiChoi2 = nguoiChoi2;
        this.turn = 'x';
        this.timeLimit = 15;
    }

    public NguoiChoi getNguoiChoi1() {
        return nguoiChoi1;
    }

    public void setNguoiChoi1(NguoiChoi nguoiChoi1) {
        this.nguoiChoi1 = nguoiChoi1;
    }

    public NguoiChoi getNguoiChoi2() {
        return nguoiChoi2;
    }

    public void setNguoiChoi2(NguoiChoi nguoiChoi2) {
        this.nguoiChoi2 = nguoiChoi2;
    }

    public int checkWin(){
        // Tất cả các nước đi gửi đến server đều phải được kiểm tra hợp lệ trước
        int[] counts = new int[8];

        // No one moved
        if (nuocDiGanNhat == null)
            return -1;

        int player = banCo[nuocDiGanNhat.getY()][nuocDiGanNhat.getX()];

        // Left
        int i = nuocDiGanNhat.getY(), j = nuocDiGanNhat.getX();

        while (j >= 0 && banCo[i][j] == player){
            j --;
        }
        counts[L] = nuocDiGanNhat.getX() - j;

        if (counts[L] + 1 >= 5){
            return player;
        }

        // Right
        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        while (j < banCo[i].length && banCo[i][j] == player){
            j ++;
        }
        counts[R] = j - nuocDiGanNhat.getX();

        if (counts[L] + counts[R] - 1 >= 5){
            return player;
        }

        // Up
        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        while (i >= 0 && banCo[i][j] == player){
            i --;
        }
        counts[U] = nuocDiGanNhat.getY() - i;

        if (counts[U] >= 5){
            return player;
        }

        // Down
        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        while (i < banCo.length && banCo[i][j] == player){
            i ++;
        }
        counts[D] = i - nuocDiGanNhat.getY();

        if (counts[D] + counts[U] - 1 >= 5){
            return player;
        }

        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        // Up left
        while (i >= 0 && j >= 0 && banCo[i][j] == player){
            i --;
            j --;
        }
        counts[UL] = nuocDiGanNhat.getY() - i;

        if (counts[UL] >= 5){
            return player;
        }

        // Down right
        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        while (i < banCo.length && j < banCo.length && banCo[i][j] == player){
            i ++;
            j ++;
        }
        counts[DR] = i - nuocDiGanNhat.getY();

        if (counts[UL] + counts[DR] - 1 >= 5){
            return player;
        }

        // Up right
        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        while (i >= 0 && j < banCo.length && banCo[i][j] == player){
            i --;
            j ++;
        }
        counts[UR] = nuocDiGanNhat.getY() - i;

        if (counts[UR] >= 5){
            return player;
        }

        // Down left
        i = nuocDiGanNhat.getY();
        j = nuocDiGanNhat.getX();

        while (i < banCo.length && j >= 0 && banCo[i][j] == player){
            i ++;
            j --;
        }
        counts[DL] = i - nuocDiGanNhat.getY();

        if (counts[UR] + counts[DL] - 1 >= 5){
            return player;
        }

        // TODO: Add check for draw using the sum of the player's total number of moves
        return -1;
    }

    public char getTurn() {
        return turn;
    }

    public void setTurn(char turn) {
        this.turn = turn;
    }

    public ToaDo getNuocDiGanNhat() {
        return nuocDiGanNhat;
    }

    public void setNuocDiGanNhat(ToaDo nuocDiGanNhat) {
        this.nuocDiGanNhat = nuocDiGanNhat;
    }

    public void move() {
        banCo[nuocDiGanNhat.getY()][nuocDiGanNhat.getX()] = turn;
        changeTurn();
    }

    public void changeTurn() {
        turn = turn == 'x' ? 'o' : 'x';
    }
}
