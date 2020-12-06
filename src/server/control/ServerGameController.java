package server.control;

import model.BangXepHang;
import model.NguoiChoi;
import model.ToaDo;
import server.control.DAO.BangXepHangDAO;

public class ServerGameController {
    private int[][] banCo;
    private static final int L = 0, R = 1, U = 2, D = 3, UL = 4, UR = 5, DL = 6, DR = 7;
    private char luot;
    private ToaDo nuocDiGanNhat;
    private volatile int traLoi;
    private volatile boolean dauLai;
    private int soBuocDi1, soBuocDi2;
    private BangXepHang nguoiChoi1, nguoiChoi2;
    private volatile boolean thoatGame1, thoatGame2;

    public ServerGameController(NguoiChoi nguoiChoi1, NguoiChoi nguoiChoi2, int kichCo){
        this.banCo = new int[kichCo][kichCo];
        this.nuocDiGanNhat = null;
        BangXepHangDAO bangXepHangDAO = new BangXepHangDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password");

        this.nguoiChoi1 = bangXepHangDAO.getRankings(nguoiChoi1.getId());
        this.nguoiChoi2 = bangXepHangDAO.getRankings(nguoiChoi2.getId());

        this.luot = 'x';
        this.traLoi = 0;
        this.dauLai = true;
        this.thoatGame1 = false;
        this.thoatGame2 = false;
    }

    public NguoiChoi getNguoiChoi1() {
        return nguoiChoi1;
    }

    public void setNguoiChoi1(NguoiChoi nguoiChoi1) {
        this.nguoiChoi1 = (BangXepHang) nguoiChoi1;
    }

    public NguoiChoi getNguoiChoi2() {
        return nguoiChoi2;
    }

    public void setNguoiChoi2(NguoiChoi nguoiChoi2) {
        this.nguoiChoi2 = (BangXepHang) nguoiChoi2;
    }

    public int checkWin(){
        // Tất cả các nước đi gửi đến server đều phải được kiểm tra hợp lệ trước
        boolean won = false;

        int[] counts = new int[8];


        // If one player left
        if (thoatGame1 || thoatGame2) {
           return -2;
        }

        else {
            if (nuocDiGanNhat == null)
                return -1;

            int player = banCo[nuocDiGanNhat.getY()][nuocDiGanNhat.getX()];


            // Left
            int i = nuocDiGanNhat.getY(), j = nuocDiGanNhat.getX();

            while (j >= 0 && banCo[i][j] == player) {
                j--;
            }
            counts[L] = nuocDiGanNhat.getX() - j - 1;

            if (counts[L] + 1 >= 5) {
                won = true;
            }

            // Right
            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            while (j < banCo[i].length && banCo[i][j] == player) {
                j++;
            }
            counts[R] = j - nuocDiGanNhat.getX() - 1;

            if (counts[L] + counts[R] + 1 >= 5) {
                won = true;
            }

            // Up
            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            while (i >= 0 && banCo[i][j] == player) {
                i--;
            }
            counts[U] = nuocDiGanNhat.getY() - i - 1;

            if (counts[U] + 1 >= 5) {
                won = true;
            }

            // Down
            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            while (i < banCo.length && banCo[i][j] == player) {
                i++;
            }
            counts[D] = i - nuocDiGanNhat.getY() - 1;

            if (counts[D] + counts[U] + 1 >= 5) {
                won = true;
            }

            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            // Up left
            while (i >= 0 && j >= 0 && banCo[i][j] == player) {
                i--;
                j--;
            }
            counts[UL] = nuocDiGanNhat.getY() - i - 1;

            if (counts[UL] + 1 >= 5) {
                won = true;
            }

            // Down right
            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            while (i < banCo.length && j < banCo.length && banCo[i][j] == player) {
                i++;
                j++;
            }
            counts[DR] = i - nuocDiGanNhat.getY() - 1;

            if (counts[UL] + counts[DR] + 1 >= 5) {
                won = true;
            }

            // Up right
            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            while (i >= 0 && j < banCo.length && banCo[i][j] == player) {
                i--;
                j++;
            }
            counts[UR] = nuocDiGanNhat.getY() - i - 1;

            if (counts[UR] + 1 >= 5) {
                won = true;
            }

            // Down left
            i = nuocDiGanNhat.getY();
            j = nuocDiGanNhat.getX();

            while (i < banCo.length && j >= 0 && banCo[i][j] == player) {
                i++;
                j--;
            }
            counts[DL] = i - nuocDiGanNhat.getY() - 1;

            if (counts[UR] + counts[DL] - 1 >= 5) {
                won = true;
            }

            if (soBuocDi1 + soBuocDi2 == banCo.length * banCo.length) {
                nguoiChoi1.setTongDiem((float) (nguoiChoi1.getTongDiem() + 0.5));
                nguoiChoi2.setTongDiem((float) (nguoiChoi2.getTongDiem() + 0.5));
                return 0;
            }

            if (won) {
                BangXepHang winner, loser;
                int soBuocDiWinner, soBuocDiLoser;
                if (player == 'x') {
                    winner = nguoiChoi1;
                    loser = nguoiChoi2;
                    soBuocDiWinner = soBuocDi1;
                    soBuocDiLoser = soBuocDi2;
                } else {
                    winner = nguoiChoi2;
                    loser = nguoiChoi1;
                    soBuocDiWinner = soBuocDi2;
                    soBuocDiLoser = soBuocDi1;
                }

                winner.setTongDiem(winner.getTongDiem() + 1);

                winner.setSoTranDauDaChoi(winner.getSoTranDauDaChoi() + 1);
                loser.setSoTranDauDaChoi(loser.getSoTranDauDaChoi() + 1);

                winner.setSoTranThang(winner.getSoTranThang() + 1);
                loser.setSoTranThua(loser.getSoTranThua() + 1);

                winner.setTongDiemDoiThu(winner.getTongDiemDoiThu() + loser.getTongDiem());
                loser.setTongDiemDoiThu(loser.getTongDiemDoiThu() + winner.getTongDiem());

                winner.setTongSoNuocDiTranThang(winner.getTongSoNuocDiTranThang() + soBuocDiWinner);
                loser.setTongSoNuocDiTranThua(loser.getTongSoNuocDiTranThua() + soBuocDiLoser);

                BangXepHangDAO bangXepHangDAO = new BangXepHangDAO("jdbc:mysql://127.0.0.1:3306/?useSSL=false", "root", "password");
                bangXepHangDAO.updateTable(winner);
                bangXepHangDAO.updateTable(loser);

                bangXepHangDAO.closeConnection();

                return player;
            }

        }
        return -1;
    }

    public char getLuot() {
        return luot;
    }

    public void setLuot(char luot) {
        this.luot = luot;
    }

    public ToaDo getNuocDiGanNhat() {
        return nuocDiGanNhat;
    }

    public void setNuocDiGanNhat(ToaDo nuocDiGanNhat) {
        this.nuocDiGanNhat = nuocDiGanNhat;
    }

    public void move() {
        if (luot == 'x') {
            soBuocDi1++;
        } else {
            soBuocDi2++;
        }
        banCo[nuocDiGanNhat.getY()][nuocDiGanNhat.getX()] = luot;
        changeTurn();
    }

    public void changeTurn() {
        luot = luot == 'x' ? 'o' : 'x';
    }

    public synchronized void rematch(boolean rematch) {
        if (traLoi == 2)
            traLoi = 0;
        traLoi++;
            this.dauLai = this.dauLai & rematch;
        if (traLoi == 2 && this.dauLai) {
            this.banCo = new int[banCo.length][banCo.length];
            this.nuocDiGanNhat = null;
            this.luot = 'x';
            this.dauLai = true;
        }
    }

    public int getTraLoi() {
        return traLoi;
    }

    public void thoatGame(int id) {
        if (id == nguoiChoi1.getId()) {
            thoatGame1 = true;
        }

        else {
            thoatGame2 = true;
        }
    }
    public boolean getDauLai() {
        synchronized ((Object) this.dauLai) {
            return dauLai;
        }
    }
}
