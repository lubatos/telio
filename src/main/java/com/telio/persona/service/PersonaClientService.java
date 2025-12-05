//package com.telio.persona.service;
//
//public class PersonaClientService {
//}


package com.telio.persona.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.SchemaOutputResolver;
import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.jboss.resteasy.reactive.client.api.RestClientBuilder;
import java.net.URI;
import java.net.http.*;
        import java.time.Duration;

@ApplicationScoped
public class PersonaClientService {

    @ConfigProperty(name = "persona.api.url")
    String personaApiUrl;

    @ConfigProperty(name = "persona.api.key")
    String personaApiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    /** Crear Inquiry en Persona */
    public JsonNode createInquiry() throws Exception {
        String jsonBody = """
    {
      "data": {
        "type": "inquiry",
        "attributes": {
          "inquiry-template-id": "%s"
        }
      }
    }
    """.formatted("itmpl_NXMuR5XXJVgPdZHu445BcSF7NiEs");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.withpersona.com/api/v1/inquiries"))
                .header("Authorization", "Bearer " + personaApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Persona /inquiries Response:");
        System.out.println(response.body());

        if (response.statusCode() >= 400)
            throw new RuntimeException("Persona ERROR: " + response.body());
        System.out.println("devuelve");
        return mapper.readTree(response.body());
    }


//    public JsonNode createInquiry() throws Exception {
//
//        String jsonBody = """
//    {
//      "data": {
//        "type": "inquiry",
//        "attributes": {
//          "inquiry-template-id": "%s",
//          "environment": "sandbox"
//        }
//      }
//    }
//    """.formatted("itmpl_NXMuR5XXJVgPdZHu445BcSF7NiEs");
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.withpersona.com/api/v1/inquiries"))
//                .header("Accept", "application/json")
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + personaApiKey)
//                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
//                .build();
//        System.out.println(":::::::: "+request.toString());
//        System.out.println(":::::::: "+ request.bodyPublisher());
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//
//        // DEBUG PARA SABER QUÉ RESPONDE PERSONA EXACTAMENTE
//        System.out.println("🔍 Persona RAW response:");
//        System.out.println(response.body());
//
//        if (response.statusCode() >= 400) {
//            System.out.println("ERROR !!");
//            throw new RuntimeException("Persona API ERROR: " + response.statusCode() + " - " + response.body());
//        }
//        System.out.println("RESPUESTA !!"+response.body());
//        return mapper.readTree(response.body());
//    }


    /** Obtener estado completo del Inquiry */
    public JsonNode getInquiry(String inquiryId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(personaApiUrl + "/api/v1/inquiries/" + inquiryId))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + personaApiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }


    /** Paso 2: crear session (para obtener client-token) */
    public JsonNode createSession(String inquiryId) throws Exception {
        String jsonBody = """
    {
      "data": {
        "type": "session",
        "attributes": {
          "inquiry-id": "%s"
        }
      }
    }
    """.formatted(inquiryId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.withpersona.com/api/v1/sessions"))
                .header("Authorization", "Bearer " + personaApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(" Persona /sessions Response:");
        System.out.println(response.body());

        if (response.statusCode() >= 400)
            throw new RuntimeException("Persona ERROR: " + response.body());

        return mapper.readTree(response.body());
    }


}
