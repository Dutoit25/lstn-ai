package za.co.lstn.agent;

import javax.mail.*;
import java.util.Properties;



public class EmailSender {
    public static void main(String[] args) throws NoSuchProviderException {

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");
        String username = "dutoit.appelcryn@gmail.com";
        String password = "*****";

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        Store store = session.getStore("imaps");
//        store.connect(host, username, appPassword);;


    }
}
