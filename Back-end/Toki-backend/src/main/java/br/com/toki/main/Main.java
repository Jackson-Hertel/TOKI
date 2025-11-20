package br.com.toki.main;

import br.com.toki.servlet.UsuarioServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {

        // Cria servidor na porta 8080
        Server server = new Server(8080);

        // Contexto principal
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Configura caminho para recursos estáticos (front-end)
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        // Diretório base para arquivos estáticos: src/main/resources/static
        defaultServlet.setInitParameter("resourceBase", Main.class.getClassLoader().getResource("static").toExternalForm());
        defaultServlet.setInitParameter("dirAllowed", "true"); // Permite navegação por pastas
        context.addServlet(defaultServlet, "/"); // Servir todos arquivos a partir da raiz

        // Adiciona servlet de usuários
        context.addServlet(UsuarioServlet.class, "/usuario/*");

        // Define o contexto no servidor
        server.setHandler(context);

        // Inicia o servidor
        server.start();
        System.out.println("Servidor iniciado em http://localhost:8080/login_cadastro/login.html");
        server.join();
    }
}
