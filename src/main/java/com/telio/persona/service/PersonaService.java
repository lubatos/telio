//package com.telio.persona.service;
//
//public class PersonaService {
//}

package com.telio.persona.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.telio.persona.dto.StartInquiryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PersonaService {

    @Inject
    PersonaClientService personaClient;

    /** Iniciar flujo: crear inquiry y retornar token */
    public StartInquiryResponse startFlow() throws Exception {
        System.out.println("paso 2");
        try {
            JsonNode result = personaClient.createInquiry();
            System.out.println("paso 3");
            String inquiryId = result.get("data").get("id").asText();
            String clientToken = result.get("data").get("attributes").get("client-token").asText();
            String templateId = result.get("data").get("attributes").get("template-id").asText();
            System.out.println("paso 4");
            return new StartInquiryResponse(inquiryId, clientToken, templateId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }





//    public StartInquiryResponse startFlow() throws Exception {
//        System.out.println("paso 2");
//        try {
//            JsonNode result = personaClient.createInquiry();
//            System.out.println("paso 3");
//            String inquiryId = result.get("data").get("id").asText();
//            String clientToken = result.get("data").get("attributes").get("client-token").asText();
//            String templateId = result.get("data").get("attributes").get("template-id").asText();
//            System.out.println("paso 4");
//            return new StartInquiryResponse(inquiryId, clientToken, templateId);
//        }catch (Exception e){
//            throw new Exception(e.getMessage());
//        }
//
//    }
//
    /** Obtener estado final del inquiry */
    public JsonNode getStatus(String inquiryId) throws Exception {
        return personaClient.getInquiry(inquiryId);
    }

    /** Procesar webhooks */
    public void processWebhook(JsonNode event) {
        String eventType = event.get("type").asText();
        System.out.println("webhook:1 ");
        System.out.println("webhook:2 ");
        System.out.println("webhook:3 ");
        System.out.println("webhook:4 ");
        System.out.println("webhook:5 ");

        System.out.println("Persona webhook recibido: " + eventType);

        // Aquí luego podrás manejar lógica de BD.
        // Por ahora solo imprime la data.
        System.out.println(event.toPrettyString());
    }
}
