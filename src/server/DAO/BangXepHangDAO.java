package server.DAO;

import model.BangXepHang;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BangXepHangDAO extends DAO {

    public BangXepHangDAO(String dbUrl, String username, String password) {
        super(dbUrl, username, password);
    }

    public BangXepHang getRankings(int id) {
        try {
            String query = "SELECT tenDangNhap, tongSoDiem, tongDiemDoiThu, tongSoNuocDiTranThang, tongSoNuocDiTranThua, tongSoTranDau, tongSoTranThang, tongSoTranThua " +
                    "FROM LTM.tbl_nguoiChoi INNER JOIN LTM.tbl_bangXepHang " +
                    "ON LTM.tbl_bangXepHang.tbl_nguoiChoiID = LTM.tbl_nguoiChoi.id " +
                    "ORDER BY tongSoDiem, tongDiemDoiThu, tongSoNuocDiTranThang, tongSoNuocDiTranThua";

            PreparedStatement ps = this.con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BangXepHang bangXepHang = new BangXepHang(rs.getString(1));
                bangXepHang.setTongSoDiem(rs.getInt(2));
                bangXepHang.setTongDiemDoiThu( (float) rs.getInt(3) );
                bangXepHang.setTongSoNuocDiTranThang((rs.getInt(4)));
                bangXepHang.setTongSoNuocDiTranThua(rs.getInt(5));
                bangXepHang.setSoTranDauDaChoi(rs.getInt(6));
                bangXepHang.setSoTranThang(rs.getInt(7));
                bangXepHang.setSoTranThua(rs.getInt(8));
                return bangXepHang;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public ArrayList<BangXepHang> getRankings() {
        ArrayList<BangXepHang> res = new ArrayList<>();
        try {
            String query = "SELECT tenDangNhap, tongSoDiem, tongDiemDoiThu, tongSoNuocDiTranThang, tongSoNuocDiTranThua, tongSoTranDau, tongSoTranThang, tongSoTranThua " +
                    "FROM LTM.tbl_nguoiChoi INNER JOIN LTM.tbl_bangXepHang " +
                    "ON LTM.tbl_bangXepHang.tbl_nguoiChoiID = LTM.tbl_nguoiChoi.id " +
                    "ORDER BY tongSoDiem, tongDiemDoiThu, tongSoNuocDiTranThang, tongSoNuocDiTranThua";
            PreparedStatement preparedStatement = this.con.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                BangXepHang bangXepHang = new BangXepHang(rs.getString(1));
                bangXepHang.setTongSoDiem(rs.getInt(2));
                bangXepHang.setTongDiemDoiThu( (float) rs.getInt(3) );
                bangXepHang.setTongSoNuocDiTranThang((rs.getInt(4)));
                bangXepHang.setTongSoNuocDiTranThua(rs.getInt(5));
                bangXepHang.setSoTranDauDaChoi(rs.getInt(6));
                bangXepHang.setSoTranThang(rs.getInt(7));
                bangXepHang.setSoTranThua(rs.getInt(8));

                res.add(bangXepHang);
            }
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void updateTable(BangXepHang bangXepHang) {
        try {
            String query = "UPDATE LTM.tbl_bangXepHang " +
                    "SET tongSoDiem = ? , tongDiemDoiThu = ?, tongSoNuocDiTranThang = ?, tongSoNuocDiTranThua = ?, tongSoTranDau = ?, tongSoTranThang = ?, tongSoTranThua = ? " +
                    "WHERE tbl_nguoiChoiID = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setFloat(1, bangXepHang.getTongSoDiem());
            ps.setFloat(2, bangXepHang.getTongDiemDoiThu());
            ps.setInt(3, bangXepHang.getTongSoNuocDiTranThang());
            ps.setInt(4, bangXepHang.getTongSoNuocDiTranThua());
            ps.setInt(5, bangXepHang.getSoTranDauDaChoi());
            ps.setInt(6, bangXepHang.getSoTranThang());
            ps.setInt(7, bangXepHang.getSoTranThua());

            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
