package br.com.toki.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
            "jdbc:h2:tcp://localhost/C:/Projeto_TOKI/Back-end/Toki-backend/database/toki_db;" +
                    "AUTO_SERVER=TRUE;" +
                    "DB_CLOSE_DELAY=-1;" +
                    "IFEXISTS=FALSE;" +
                    "MODE=MySQL;" +
                    "DATABASE_TO_UPPER=false";

    private static final String USER = "sa";
    private static final String PASS = "";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco H2", e);
        }
    }
}
