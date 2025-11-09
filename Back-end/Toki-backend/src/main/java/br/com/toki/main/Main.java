package br.com.toki.main;

import br.com.toki.service.UsuarioService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import br.com.toki.servlet.UsuarioServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        UsuarioService service = new UsuarioService();
        service.criarTabela();

        Server server = new Server(8080); // Porta do backend

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new UsuarioServlet()), "/api/usuarios/*");


        server.setHandler(handler);
        server.start();
        System.out.println("Backend rodando em http://localhost:8080");
        server.join();
    }
}

