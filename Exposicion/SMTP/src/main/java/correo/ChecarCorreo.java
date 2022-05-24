package correo;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class ChecarCorreo {
    public static void revisaCorreos(String host, String storeType, String user,
            String password) {
        try {

            // Crea un objeto de propiedades
            Properties properties = new Properties();

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            // Crea el "store" object y se conecta con el servidor POP
            Store store = emailSession.getStore("pop3s");

            store.connect(host, user, password);

            // Abre el folder de correos entrantes
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // Obtiene todos los mensajes en un arreglo
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Numero de correo " + (i + 1));
                System.out.println("Asunto: " + message.getSubject());
                System.out.println("Emisor: " + message.getFrom()[0]);
                System.out.println("Texto: " + message.getContent().toString());

            }

            // Cierra el objeto y el folder
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String host = "pop.gmail.com"; // Servidor de correo entrante de Gmail
        String mailStoreType = "pop3";
        String username = "correo@bonito.com";
        String password = "****";

        revisaCorreos(host, mailStoreType, username, password);

    }
}
