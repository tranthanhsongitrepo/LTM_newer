package server.control.DAO;

import model.NguoiChoi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NguoiChoiDAO extends DAO {

    public NguoiChoiDAO(String dbUrl, String username, String password) {
        super(dbUrl, username, password);
    }

    public int checkLogin(NguoiChoi nguoiChoi){
        String query = "SELECT id FROM LTM.tbl_nguoiChoi WHERE tenDangNhap = ? AND matKhau = ?";
            try {
                PreparedStatement preparedStatement = this.con.prepareStatement(query);
                preparedStatement.setString(1, nguoiChoi.getTenDangNhap());
                preparedStatement.setString(2, nguoiChoi.getMatKhau());
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        return -1;
    }
}
