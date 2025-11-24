package br.com.toki.main;

import br.com.toki.filter.CorsFilter;
import br.com.toki.servlet.EnviarCodigoServlet;
import br.com.toki.servlet.RedefinirSenhaServlet;
import br.com.toki.servlet.UsuarioServlet;
import br.com.toki.servlet.EventoServlet;
import br.com.toki.service.UsuarioService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {

        // 0️⃣ Iniciar H2 TCP Server
        org.h2.tools.Server tcpServer = org.h2.tools.Server.createTcpServer(
                "-tcpAllowOthers", "-tcpPort", "9092").start();
        org.h2.tools.Server webServer = org.h2.tools.Server.createWebServer(
                "-webAllowOthers", "-webPort", "8082").start();
        System.out.println("H2 TCP server rodando em: " + tcpServer.getURL());
        System.out.println("H2 Web console rodando em: " + webServer.getURL());

        // 1️⃣ Criar tabela ao iniciar
        UsuarioService service = new UsuarioService();
        service.criarTabela();

        // 2️⃣ Criar o servidor Jetty
        Server server = new Server(8080);

        // 3️⃣ Criar contexto COM SESSÕES
        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // 4️⃣ Configurar arquivos estáticos (frontend)
        context.setResourceBase("C:/Projeto_TOKI/Back-end/Toki-backend/Toki-frontend");

        // DefaultServlet serve os arquivos HTML/CSS/JS
        ServletHolder staticHolder = new ServletHolder("static", DefaultServlet.class);
        staticHolder.setInitParameter("dirAllowed", "true");
        context.addServlet(staticHolder, "/");

        // Página inicial
        context.setWelcomeFiles(new String[]{"login_cadastro/login.html"});

        // 5️⃣ Filtro de CORS
        context.addFilter(CorsFilter.class, "/*", null);

        // 6️⃣ Registrar Servlets do backend
        context.addServlet(new ServletHolder(new UsuarioServlet()), "/toki/usuario/*");
        context.addServlet(new ServletHolder(new EventoServlet()), "/toki/evento/*");
        context.addServlet(new ServletHolder(new RedefinirSenhaServlet()), "/redefinir/*");
        context.addServlet(new ServletHolder(new EnviarCodigoServlet()), "/codigo/*");

        // 7️⃣ Iniciar servidor
        server.setHandler(context);
        server.start();
        System.out.println("Servidor iniciado em: http://localhost:8080/");
        server.join();
    }
}
