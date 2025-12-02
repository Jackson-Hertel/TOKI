package br.com.toki.main;

import br.com.toki.dao.UsuarioDAO;
import br.com.toki.dao.EventoDAO;
import br.com.toki.servlet.UsuarioServlet;
import br.com.toki.servlet.EventoServlet;
import br.com.toki.servlet.RedefinirSenhaServlet;
import br.com.toki.servlet.EnviarCodigoServlet;
import br.com.toki.filter.CorsFilter;

import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
// outros imports permanecem iguais

public class Main {

    public static void main(String[] args) throws Exception {

        // ============================
        // INICIA H2
        // ============================
        org.h2.tools.Server tcpServer = org.h2.tools.Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092").start();
        org.h2.tools.Server webServer = org.h2.tools.Server.createWebServer("-webAllowOthers", "-webPort", "8082").start();

        // ============================
        // CRIA TABELAS
        // ============================
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.criarTabela();
        EventoDAO eventoDAO = new EventoDAO();
        eventoDAO.criarTabela();

        System.out.println("Tabelas criadas ou já existentes.");

        // ============================
        // CONFIGURAÇÃO JETTY COM LIMITE MAIOR
        // ============================
        Server server = new Server(); // servidor sem porta ainda

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setRequestHeaderSize(32768); // aumenta para 32KB

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(8080); // define porta
        server.addConnector(connector);

        // ============================
        // CONTEXTO E SERVLETS
        // ============================
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.getSessionHandler().getSessionCookieConfig().setPath("/");
        context.getSessionHandler().getSessionCookieConfig().setHttpOnly(true);
        context.getSessionHandler().getSessionCookieConfig().setSecure(false);

        String webappPath = Main.class.getClassLoader().getResource("webapp").toExternalForm();
        context.setResourceBase(webappPath);
        context.setWelcomeFiles(new String[]{"login_cadastro/login.html"});

        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("dirAllowed", "false");
        context.addServlet(staticHolder, "/");

        context.addFilter(CorsFilter.class, "/*", null);

        ServletHolder usuarioHolder = new ServletHolder(new UsuarioServlet());
        usuarioHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement(null,5*1024*1024,10*1024*1024,1024*1024)
        );
        context.addServlet(usuarioHolder, "/toki/usuario/*");
        context.addServlet(new ServletHolder(new EventoServlet()), "/toki/evento/*");
        context.addServlet(new ServletHolder(new RedefinirSenhaServlet()), "/redefinir/*");
        context.addServlet(new ServletHolder(new EnviarCodigoServlet()), "/codigo/*");

        // ============================
        // INICIAR SERVIDOR
        // ============================
        server.setHandler(context);
        server.start();
        System.out.println("Servidor iniciado em http://localhost:8080/");
        System.out.println("H2 Console em http://localhost:8082/");
        server.join();
    }
}
