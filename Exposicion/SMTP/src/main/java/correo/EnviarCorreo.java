package correo;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EnviarCorreo {
    public static void main(String[] args) {
        // Receptores
        String[] tos = {
                "rsantiagoa1601@alumno.ipn.mx",
                "ccoronav1500@alumno.ipn.mx",
                "acastilloh1500@alumno.ipn.mx",
                "marangol1500@alumno.ipn.mx",
                "ealvarezl2100tmp@alumnoguinda.mx",
                "mcastillof1700@alumno.ipn.mx",
                "aalejog1800@alumno.ipn.mx",
                "ddiazg1500@alumno.ipn.mx",
                "iavilar1500@alumno.ipn.mx",
                "sarellanoa1700@alumno.ipn.mx",
                "jvillalobosa1500@alumno.ipn.mx",
                "aalcantarm1500@alumno.ipn.mx",
                "echavezh1501@alumno.ipn.mx",
                "rhernandezj1500@alumno.ipn.mx",
                "ecuevass1600@alumno.ipn.mx",
                "ajuarezm1502@alumno.ipn.mx",
                "btorresm1802@alumno.ipn.mx",
                "creyesg1500@alumno.ipn.mx",
                "ahernandezr1401@alumno.ipn.mx",
                "mmoralesc1501@alumno.ipn.mx",
                "hjaimev1300@alumno.ipn.mx",
                "sgonzalezg1502@alumno.ipn.mx",
                "szunigag1400@alumno.ipn.mx",
                "mgarcias1504@alumno.ipn.mx",
                "ualemanv1300@alumno.ipn.mx",
                "cmunozc1500@alumno.ipn.mx",
                "ochavezl1500@alumno.ipn.mx",
                "eugaldel1500@alumno.ipn.mx",
                "jcontrerasm1300@alumno.ipn.mx",
                "gdelgador1500@alumno.ipn.mx",
                "ylimap1601@alumno.ipn.mx",
                "amendozac1904@alumno.ipn.mx",
                "mdiazr2100tmp@alumnoguinda.mx",
                "asanjuanv1500@alumno.ipn.mx",
                "acruzc1400@alumno.ipn.mx"
        };

        // Emisor
        String from = "correo@bonito.com";

        final String username = "correo@bonito.com";
        final String password = "****";

        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Objeto sesion.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        for (String to : tos) {
            try {
                // Creamos un mensaje
                Message message = new MimeMessage(session);

                // Colocamos desde donde se envia
                message.setFrom(new InternetAddress(from));

                // Agregamos receptor
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // El asunto
                message.setSubject("Correo de ejemplo SMTP");

                // Creamos el mensaje
                BodyPart messageBodyPart = new MimeBodyPart();

                // Configuramos el texto
                messageBodyPart.setText("Hola, este es un mensaje con imagen");

                // Creamos un multimedia
                Multipart multipart = new MimeMultipart();

                // Agregamos el texto al multimedia
                multipart.addBodyPart(messageBodyPart);

                // Agregamos el archivo al cuerpo
                messageBodyPart = new MimeBodyPart();
                String filename = "./Lunes.jpg";
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);

                // Lo agregamos el mensaje
                message.setContent(multipart);

                // Y lo enviamos
                Transport.send(message);

                System.out.println("Mensaje enviado a: " + to);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
