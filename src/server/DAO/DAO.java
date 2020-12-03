package server.DAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    protected Connection con;
    private ServerSocket serverSocket;
    protected ObjectInputStream is;
    protected ObjectOutputStream os;

    public DAO(String dbUrl, String username, String password) {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(dbUrl, username, password);
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
