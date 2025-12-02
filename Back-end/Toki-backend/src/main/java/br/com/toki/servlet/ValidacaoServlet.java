package br.com.toki.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuario/validar")
public class ValidacaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Sempre retorna sucesso
        response.setStatus(200);
        response.getWriter().write("{\"mensagem\":\"✔ Conta validada automaticamente!\"}");
    }
}
