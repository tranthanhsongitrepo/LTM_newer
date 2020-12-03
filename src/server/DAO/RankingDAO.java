package server.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RankingDAO extends DAO {

    public RankingDAO(String dbUrl, String username, String password) {
        super(dbUrl, username, password);
    }

    public ArrayList<Object[]> getRankings() {
        ArrayList<Object[]> res = new ArrayList<>();
        try {
            String query = "SELECT * FROM LTM.tbl_bangXepHang ORDER BY tongSoDiem, trungBinhDiemDoiThu, trungBinhSoNuocDiTranThang, trungBinhSoNuocDiTranThua";
            PreparedStatement preparedStatement = this.con.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                res.add(new Object[] {rs.getInt(1), rs.getFloat(2), rs.getFloat(3), rs.getFloat(4)});
            }
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
