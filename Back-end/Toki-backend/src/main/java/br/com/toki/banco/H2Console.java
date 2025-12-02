package br.com.toki.banco;

import org.h2.tools.Server;

public class H2Console {
    public static void main(String[] args) throws Exception {
        // Inicia o H2 TCP server em outra porta
        Server tcpServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9093").start();
        // Inicia o H2 Web console
        Server webServer = Server.createWebServer("-webAllowOthers", "-webPort", "8082").start();

        System.out.println("H2 TCP server rodando em: " + tcpServer.getURL());
        System.out.println("H2 Web console rodando em: " + webServer.getURL());
    }
}
