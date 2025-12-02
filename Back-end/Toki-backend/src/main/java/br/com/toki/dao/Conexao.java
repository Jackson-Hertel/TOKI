package br.com.toki.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // Banco H2 dentro da pasta do projeto (ex.: pendrive/TOKI/database)
    private static final String URL =
            "jdbc:h2:file:./database/toki_db;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false";
    private static final String USER = "sa";
    private static final String PASS = "";

    // Método estático para usar em todo DAO
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
