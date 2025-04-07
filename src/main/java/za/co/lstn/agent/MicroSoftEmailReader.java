package za.co.lstn.agent;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MicroSoftEmailReader {

    public static String getAuthToken(String tanantId,String clientId,String client_secret) {
//            throws ClientProtocolException, IOException {
//        CloseableHttpClient client = HttpClients.createDefault();
//        HttpPost loginPost = new HttpPost("https://login.microsoftonline.com/" + tanantId + "/oauth2/v2.0/token");
//        String scopes = "https://outlook.office365.com/.default";
//        String encodedBody = "client_id=" + clientId + "&scope=" + scopes + "&client_secret=" + client_secret
//                + "&grant_type=client_credentials";
//        loginPost.setEntity(new StringEntity(encodedBody, ContentType.APPLICATION_FORM_URLENCODED));
//        loginPost.addHeader(new BasicHeader("cache-control", "no-cache"));
//        CloseableHttpResponse loginResponse = client.execute(loginPost);
//        InputStream inputStream = loginResponse.getEntity().getContent();
//        byte[] response = readAllBytes(inputStream);
//        ObjectMapper objectMapper = new ObjectMapper();
//        JavaType type = objectMapper.constructType(
//                objectMapper.getTypeFactory().constructParametricType(Map.class, String.class, String.class));
//        Map<String, String> parsed = new ObjectMapper().readValue(response, type);
//        return parsed.get("access_token");
        return "null";
    }


    public static void main(String[] args) {
        // Email server properties (Microsoft - adjust as needed)
        String host = "outlook.office365.com"; // Microsoft Office 365 server
        String port = "993"; // Standard IMAPS port
        String username = "dutoita2025@outlook.com"; // Your email address
        String password = "Pampoen1961#"; // Your email password (use app passwords if 2FA is enabled)

        // Properties for connecting to IMAP server
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", port);
        props.put("mail.imaps.starttls.enable", "false");
        props.put("mail.imaps.ssl.trust", "*"); // Use with caution in production

        // Setting up the session
        Session session = Session.getInstance(props);
        session.setDebug(true); // Optional: Enables debug output for the session

        try {
            // Connect to email store using the IMAPS protocol
            Store store = session.getStore("imap");
            store.connect(host, username, password);

            // Access the "INBOX" folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY); // Open folder in read-only mode

            // Retrieve messages from inbox
            Message[] messages = inbox.getMessages();

            System.out.println("Total messages in Inbox: " + messages.length);

            // Iterate through messages and print basic information
            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];

                // Display email details
                System.out.println("=======================================");
                System.out.println("Email Number: " + (i + 1));
                System.out.println("From: " + InternetAddress.toString(message.getFrom()));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("Date: " + message.getReceivedDate());

                // Example of reading content
                if (message.isMimeType("text/plain")) {
                    System.out.println("Content: " + message.getContent());
                } else if (message.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);

                        if (bodyPart.isMimeType("text/plain")) {
                            System.out.println("Content (plain text): " + bodyPart.getContent());
                        }
                    }
                }
            }

            // Cleanup
            inbox.close(false); // Close folder but don't expunge messages
            store.close(); // Close store connection

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading emails: " + e.getMessage());
        }
    }
}