package br.com.toki.main;

import br.com.toki.service.UsuarioService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;
import br.com.toki.servlet.UsuarioServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        UsuarioService service = new UsuarioService();
        service.criarTabela();

        Server server = new Server(8080);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        // 👇 SERVE os arquivos do front-end (HTML, CSS, JS) da pasta static
        handler.setResourceBase(Main.class.getResource("/static").toExternalForm());
        handler.addServlet(DefaultServlet.class, "/");

        // 👇 Continua servindo o backend normalmente
        handler.addServlet(new ServletHolder(new UsuarioServlet()), "/api/usuarios/*");

        server.setHandler(handler);
        server.start();
        System.out.println("✅ TOKI rodando em: http://localhost:8080/login_cadastro/login.html");
        server.join();
    }
}
