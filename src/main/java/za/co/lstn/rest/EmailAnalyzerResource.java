package za.co.lstn.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import za.co.lstn.dto.EmailAnalysisDTO;
import za.co.lstn.dto.EmailMessageDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Path("/email")
public class EmailAnalyzerResource {

    private static final Logger logger = LoggerFactory.getLogger(EmailAnalyzerResource.class);

    @POST
    @Path("/analyze")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzeEmail(EmailMessageDTO email) {
        try {
            EmailAnalysisDTO result = analyzeEmailContent(email.getBody());
            return Response.ok(result).build();
        } catch (Exception e) {
            logger.error("Error analyzing email", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error analyzing email: " + e.getMessage())
                    .build();
        }
    }

    private EmailAnalysisDTO analyzeEmailContent(String emailContent) throws Exception {
        EmailAnalysisDTO result = new EmailAnalysisDTO();

        // Using Jakarta Mail (javax.mail)
        try (InputStream inputStream = new ByteArrayInputStream(emailContent.getBytes(StandardCharsets.UTF_8))) {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session, inputStream);

            result.setSubject(message.getSubject());
            result.setFrom(String.valueOf(message.getFrom()[0])); // Handle multiple senders if needed
            result.setTo(String.valueOf(message.getAllRecipients()[0])); // Handle multiple recipients

            //  Process the body (consider multipart emails)
            Object content = message.getContent();
            if (content instanceof String) {
                result.setBody((String) content);
            } else {
                // Handle multipart emails appropriately (e.g., extract text parts)
                result.setBody("Multipart email - parsing not fully implemented");
            }


        } catch (jakarta.mail.MessagingException | IOException e) {
            logger.error("Error parsing email with Jakarta Mail", e);
            throw new Exception("Error parsing email with Jakarta Mail", e); // re-throw as general exception
        }

        // Optionally:  More advanced analysis here (spam detection, sentiment analysis, etc.)

        return result;
    }




}