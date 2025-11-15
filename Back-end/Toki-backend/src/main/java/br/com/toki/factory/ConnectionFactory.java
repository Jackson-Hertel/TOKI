package br.com.toki.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionFactory {

    private static final String URL = "jdbc:h2:./tokiDB"; // cria o banco local no pendrive/pasta
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            criarTabelaSeNaoExistir(conn); // cria a tabela ao conectar
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar ao banco H2: " + e.getMessage(), e);
        }
    }

    private static void criarTabelaSeNaoExistir(Connection conn) {
        String sql = """
        CREATE TABLE IF NOT EXISTS evento (
            id IDENTITY PRIMARY KEY,
            descricao VARCHAR(255),
            data_evento VARCHAR(20),
            prioridade VARCHAR(10)
        );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }
}
