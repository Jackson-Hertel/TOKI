package br.com.toki.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:h2:~/tokiDB;DB_CLOSE_DELAY=-1"; // caminho do banco
    private static final String USER = "sa"; // usuário padrão do H2
    private static final String PASSWORD = ""; // senha padrão (vazia)

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao conectar com o banco de dados H2");
        }
    }
}
