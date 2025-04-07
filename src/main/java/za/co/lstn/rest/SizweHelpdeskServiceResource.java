package za.co.lstn.rest;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import za.co.lstn.dto.*;
import za.co.lstn.service.SizweHelpdeskService;

@Path("/lstn")
public class SizweHelpdeskServiceResource {

    @Inject
    SizweHelpdeskService promptService;

    @POST
    @Path("/email")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processEmail(EmailMessageDTO email)  {
        Log.info("Getting processEmail: " + email);
        String response = promptService.processEmail(email);

        try {
            return Response.ok().entity(response).build();
        }catch(Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testPrompt(String prompt) {
        Log.info("Getting testPrompt");
        try {
            AnswerDTO answerDTO = promptService.getRedisPrompt(prompt);
            return Response.ok().entity(answerDTO).build();
        }catch(Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/prompts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postPromptString(String prompt) {
        Log.info("Getting Prompts");
        UserPromptRequest userPrompt = new UserPromptRequest();
        userPrompt.setPrompt(prompt);
        if (prompt.trim().isEmpty()  ) {
            return null;
        }
        EmailMessageDTO emailMessageDTO = promptService.convertToEmailMessage(prompt);
        String response = promptService.processEmail(emailMessageDTO);
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswer(response);
        try {
            return Response.ok().entity(answerDTO).build();
        }catch(Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/email")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmail(String prompt) {
        Log.info("Getting email........");
        EmailMessageDTO email = new EmailMessageDTO ();
        email.setFrom("elizma@nomail.com");
        email.setBody("Good afternoon the Gold Ascend option and would like to know if my medical aid will pay for a blood pressure Monitor and if there a free screening ?");
        email.setSubject("Money available for dentistry");
        return Response.ok().entity(email).build();
    }

    @POST
    @Path("/message")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRagModelFromUserEnhancement(String message) {
        Log.info("Start updating Rag Model");
        try {
            String result = promptService.updateRagModelFromUserEnhancement(message);
            Log.info("Updated Rag Model");
            return Response.ok(result).build();
        } catch (Exception e) {
            Log.error("An error occurred while updating the RAG model: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while updating the RAG model. Please try again later.")
                    .build();
        }
    }
}
