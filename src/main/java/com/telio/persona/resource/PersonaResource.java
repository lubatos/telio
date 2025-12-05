//package com.telio.persona.resource;
//
//public class PersonaResource {
//}


package com.telio.persona.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.telio.persona.dto.StartInquiryResponse;
import com.telio.persona.service.PersonaClientService;
import com.telio.persona.service.PersonaService;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/persona")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaResource {

    @Inject
    PersonaService personaService;
    @Inject
    PersonaClientService personaClient;
    /** 1) Frontend llama para iniciar el flujo */
    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startInquiry() {
        try {
            JsonNode result = personaClient.createInquiry();
            String inquiryId = result.get("data").get("id").asText();

            return Response.ok(
                    Json.createObjectBuilder()
                            .add("inquiryId", inquiryId)
                            .build()
            ).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(
                            Json.createObjectBuilder()
                                    .add("error", true)
                                    .add("message", e.getMessage())
                                    .build()
                    )
                    .build();
        }
    }

//    @GET
//    @Path("/start")
//    public StartInquiryResponse startInquiry() throws Exception {
//        System.out.println("paso 1");
//        return personaService.startFlow();
//    }

    /** 2) Webhook de Persona */
    @POST
    @Path("/webhook")
    public void webhook(JsonNode body) {
        System.out.println("WEBHOOK");
        personaService.processWebhook(body);
    }

    /** 3) El frontend consulta estado del inquiry */
    @GET
    @Path("/status/{inquiryId}")
    public JsonNode getInquiryStatus(@PathParam("inquiryId") String inquiryId) throws Exception {

        return personaService.getStatus(inquiryId);
    }
}
