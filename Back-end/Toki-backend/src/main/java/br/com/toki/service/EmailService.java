package br.com.toki.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private final String remetente = "toki.notifications@gmail.com"; // Gmail que envia
    private final String senhaApp = "lmsl zkqr fetk jqyh";          // senha de app

    public void enviarCodigo(String destinatario, String codigo) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // evita problemas de SSL

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senhaApp);
            }
        });

        session.setDebug(true); // ativa log para ver se está enviando

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(remetente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject("Código de Recuperação TOKI");
        message.setText("Seu código para redefinir a senha é: " + codigo);

        Transport.send(message);
    }
}
