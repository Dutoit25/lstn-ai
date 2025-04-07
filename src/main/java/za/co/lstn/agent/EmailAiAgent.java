package za.co.lstn.agent;
import java.io.IOException;
import java.util.Properties;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;


public class EmailAiAgent {
    private static final String TRUE = "true";
    private static final String MAIL_POP3_HOST = "mail.pop3.host";
    private static final String MAIL_POP3_PORT = "mail.pop3.port";
    private static final String MAIL_POP3_STARTTLS_ENABLE = "mail.pop3.starttls.enable";
    private static final String MAIL_FOLDER_INBOX = "INBOX";

    public static void main(String args[]) {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "dutoit.appelcryn@gmail.com", "Redbeard17#");
            System.out.println(store);

            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            //Message messages[] = inbox.getMessages();
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message messages[] = inbox.search(ft);

            int i =0;
            for(Message message:messages)
            {

                Multipart mp = (Multipart)messages[i].getContent();
                Object p = mp.getBodyPart(i).getContent();
                String q = p.toString();//object has the body content
                System.out.println(q);//prints the body
                System.out.println( messages[i].getSubject()+ " \n"+i);i++;
            }


        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


//    public static void main(String[] args) throws Exception {
//
//        String host = "outlook.office365.com";
//        String username = "dutoita@lstn-ai.com";
//        String password = "N!933704768676oj";
//        String mailStoreType = "pop3s";
//        check(host, mailStoreType, username, password);
//    }

    public static void check(String host, String storeType, String user, String password) throws Exception {

        Store store = null;
        Folder emailFolder = null;
        try {
            Properties properties = new Properties();
            properties.put(MAIL_POP3_HOST, host);
            properties.put(MAIL_POP3_PORT, "995");
            properties.put(MAIL_POP3_STARTTLS_ENABLE, TRUE);
            Session emailSession = Session.getDefaultInstance(properties);

            // create the POP3 store object and connect with the pop server
            store = emailSession.getStore(storeType);

            store.connect(host, user, password);

            emailFolder = store.getFolder(MAIL_FOLDER_INBOX);
            emailFolder.open(Folder.READ_WRITE);

            Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            System.out.println("messages.length---" + messages.length);
            if (messages.length == 0) {
                System.out.println("No new messages found.");
            } else {
                for (int i = 0, len = messages.length; i < len; i++) {
                    Message message = messages[i];

                    boolean hasAttachments = hasAttachments(message);
                    if (hasAttachments) {
                        System.out.println(
                                "Email #" + (i + 1) + " with subject " + message.getSubject() + " has attachments.");
                        readAttachment(message);
                    } else {
                        System.out.println("Email #" + (i + 1) + " with subject " + message.getSubject()
                                + " does not have any attachments.");
                        continue;
                    }

                    Folder copyFolder = store.getFolder("copyData");
                    if (copyFolder.exists()) {
                        System.out.println("copy messages...");
                        copyFolder.copyMessages(messages, emailFolder);
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
            }

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (emailFolder != null) {
                emailFolder.close(false);
            }
            store.close();
        }
    }

    private static boolean hasAttachments(Message msg) throws Exception {
        if (msg.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart) msg.getContent();
            if (mp.getCount() > 1) {
                return true;
            }
        }

        return false;
    }

    public static void readAttachment(Message message) throws Exception {

        Multipart multiPart = (Multipart) message.getContent();
        for (int i = 0; i < multiPart.getCount(); i++) {
            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                String destFilePath = "/home/user/Documents/" + part.getFileName();
                System.out.println("Email attachement ---- " + destFilePath);
                FileOutputStream output = new FileOutputStream(destFilePath);
                InputStream input = part.getInputStream();
                byte[] buffer = new byte[4096];
                int byteRead;
                while ((byteRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, byteRead);
                }
                output.close();
            }
        }
    }


}