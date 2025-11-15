package br.com.toki.main;

import br.com.toki.service.UsuarioService;
import br.com.toki.servlet.UsuarioServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {
        UsuarioService service = new UsuarioService();
        service.criarTabela();

        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        // ✅ Serve os arquivos do classpath (resources/static) dentro do JAR
        handler.setBaseResource(
                org.eclipse.jetty.util.resource.Resource.newClassPathResource("/static")
        );

        // ✅ Define página inicial
        handler.setWelcomeFiles(new String[] { "login_cadastro/login.html" });

        // ✅ Serve arquivos estáticos
        ServletHolder staticHolder = new ServletHolder("default", new DefaultServlet());
        staticHolder.setInitParameter("dirAllowed", "false");
        handler.addServlet(staticHolder, "/");

        // ✅ Servlets
        handler.addServlet(new ServletHolder(new UsuarioServlet()), "/api/usuarios/*");

        server.setHandler(handler);
        server.start();
        System.out.println("🚀 TOKI rodando em: http://localhost:8080/");
        server.join();
    }
}
