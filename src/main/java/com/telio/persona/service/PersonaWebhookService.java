package com.telio.persona.service;

//public class PersonaWebhookService {
//}

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.StringReader;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@ApplicationScoped
public class PersonaWebhookService {

    // Valor tomado desde application.properties
    @Inject
    @ConfigProperty(name = "persona.webhook.secret", defaultValue = "")
    String configSecret;

    /**
     * Obtiene el secreto:
     * 1) Si existe VARIABLE DE ENTORNO → se usa esa
     * 2) Caso contrario → usa application.properties
     */
    private String getSecret() {
        String envSecret = System.getenv("PERSONA_WEBHOOK_SECRET");
        if (envSecret != null && !envSecret.isEmpty()) {
            return envSecret;
        }
        System.out.println("=============="+configSecret);
        return configSecret;
    }

    // ============================================================
    // VALIDACIÓN DE FIRMA (HMAC-SHA256)
    // ============================================================
    public boolean validateSignature(String body, String signature) {
        try {
            String secret = getSecret();
            System.out.println("SIGNATURE "+body);
            Mac mac = Mac.getInstance("HmacSHA256");
            System.out.println("SIGNATURE2 "+signature);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            System.out.println("SIGNATURE3");
            String expected = Base64.getEncoder().encodeToString(
                    mac.doFinal(body.getBytes(StandardCharsets.UTF_8))
            );
            System.out.println("SIGNATURE4 expected "+expected);
            return expected.equals(signature);

        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // PROCESAR EVENTOS DEL WEBHOOK
    // ============================================================
    public void processEvent(String rawBody) {
        JsonObject json = Json.createReader(new StringReader(rawBody)).readObject();
        String eventType = json.getJsonObject("data").getString("type");

        System.out.println("Webhook Persona recibido → " + eventType);

        switch (eventType) {

            case "inquiry.started":
                handleInquiryStarted(json);
                break;

            case "inquiry.completed":
                handleInquiryCompleted(json);
                break;

            case "report.completed":
                handleReportCompleted(json);
                break;

            case "report.failed":
                handleReportFailed(json);
                break;

            default:
                System.out.println("Evento no manejado: " + eventType);
        }
    }

    // ============================================================
    // LÓGICA DE EVENTOS
    // ============================================================
    private void handleInquiryStarted(JsonObject event) {
        String id = event.getJsonObject("data").getString("id");
        System.out.println("[Persona] Inquiry iniciado: " + id);
    }

    private void handleInquiryCompleted(JsonObject event) {
        JsonObject attributes = event.getJsonObject("data").getJsonObject("attributes");
        String id = event.getJsonObject("data").getString("id");
        String status = attributes.getString("status");

        System.out.println("[Persona] Inquiry completado " + id + " → estado: " + status);
    }

    private void handleReportCompleted(JsonObject event) {
        System.out.println("[Persona] Reporte completado: " + event);
    }

    private void handleReportFailed(JsonObject event) {
        System.out.println("[Persona] Reporte fallido: " + event);
    }
}