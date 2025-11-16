package br.com.toki.main;

import br.com.toki.service.UsuarioService;
import br.com.toki.servlet.UsuarioServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class Main {
    public static void main(String[] args) throws Exception {
        // Cria a tabela de usuários se não existir
        UsuarioService service = new UsuarioService();
        service.criarTabela();

        // Cria o servidor Jetty na porta 8080
        Server server = new Server(8080);

        // Handler com sessões
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");

        // Define o recurso base para os arquivos estáticos (classpath /static)
        handler.setBaseResource(Resource.newClassPathResource("/static"));

        // Define a página inicial
        handler.setWelcomeFiles(new String[]{"login_cadastro/login.html"});

        // DefaultServlet para servir arquivos estáticos
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("dirAllowed", "true"); // permite listar diretórios (opcional)
        handler.addServlet(defaultServlet, "/");

        // API de usuários
        handler.addServlet(new ServletHolder(new UsuarioServlet()), "/api/usuarios/*");

        // Inicia o servidor
        server.setHandler(handler);
        server.start();
        System.out.println("🚀 TOKI rodando em: http://localhost:8080/");
        server.join();
    }
}
