package server.DAO;

import model.NguoiChoi;
import model.ToaDo;

import java.io.IOException;

public class ToaDoDAO extends DAO {
    public ToaDoDAO(String dbUrl, String username, String password) {
        super(dbUrl, username, password);
    }
}
