package br.com.toki.servlet;

import br.com.toki.model.Usuario;
import br.com.toki.service.EmailService;
import br.com.toki.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.Random;

@WebServlet("/usuario/cadastrar")
public class CadastroServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    private final EmailService emailService = new EmailService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // validar campos
        if(nome == null || email == null || senha == null ||
                nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("Preencha todos os campos.");
            return;
        }

        // verificar se usuário já existe
        if(usuarioService.buscarUsuarioPorEmail(email) != null) {
            response.setStatus(409);
            response.getWriter().write("E-mail já cadastrado.");
            return;
        }

        // criar usuário
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha); // senha será armazenada com hash no service
        usuario.setAtivo(false);  // conta desativada até validar código
        usuarioService.adicionarUsuario(usuario); // chama service, não DAO direto

        // gerar código de verificação
        String codigo = gerarCodigo(6);
        request.getSession().setAttribute("codigoVerificacao", codigo);
        request.getSession().setAttribute("usuarioEmail", email);

        // enviar código por e-mail
        try {
            emailService.enviarCodigo(email, codigo);
        } catch (MessagingException e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("Erro ao enviar e-mail.");
            return;
        }

        response.setStatus(201);
        response.getWriter().write("✔ Código de verificação enviado!");
    }

    private String gerarCodigo(int length) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) sb.append(rand.nextInt(10));
        return sb.toString();
    }
}
