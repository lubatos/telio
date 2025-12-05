//package com.telio.maps;
//
//public class MapboxClient {
//}
package com.telio.maps;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import kong.unirest.Unirest;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@ApplicationScoped
public class MapboxClient {

    @ConfigProperty(name = "mapbox.token")
    String MAPBOX_TOKEN;

    public JsonObject getRoute(double oLat, double oLon, double dLat, double dLon) {
        System.out.println("getroute");
        String url = String.format(
                "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f"
                        + "?geometries=polyline&overview=full&access_token=%s",
                oLon, oLat, dLon, dLat, MAPBOX_TOKEN
        );
        System.out.println("URL :"+url);
        String body = Unirest.get(url).asString().getBody();

        return Json.createReader(new java.io.StringReader(body)).readObject();
    }
}
//
//package com.telio.maps;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.json.Json;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import kong.unirest.Unirest;
//import io.vertx.core.json.JsonObject;
//import io.vertx.core.json.JsonObject;
//import io.vertx.core.json.JsonArray;
//
//
//@ApplicationScoped
//public class MapboxClient {
//
//    @ConfigProperty(name = "mapbox.token")
//    String MAPBOX_TOKEN;
//
//    public JsonObject getRoute(double oLat, double oLon, double dLat, double dLon) {
//        System.out.println("getroute");
//        System.out.println("TOKEN-->"+MAPBOX_TOKEN);
//        String url = String.format(
//                "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f"
//                        + "?geometries=polyline&overview=full&access_token=%s",
//                oLon, oLat, dLon, dLat, MAPBOX_TOKEN
//        );
//        System.out.println("URL :"+url);
//        String body = Unirest.get(url).asString().getBody();
//
//        return Json.createReader(new java.io.StringReader(body)).readObject();
//    }


//    public JsonObject getRoute(double oLat, double oLon,
//                               double dLat, double dLon) {
//
//        String url = String.format(
//                "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f"
//                        + "?geometries=polyline&overview=full&access_token=%s",
//                oLon, oLat, dLon, dLat, MAPBOX_TOKEN
//        );
//
//        String body = Unirest.get(url).asString().getBody();
//
//        // Convertir respuesta JSON de Mapbox → JsonObject (Vertx)
//        return new JsonObject(body);
//    }


//    public JsonObject getRoute(double oLat, double oLon,
//                               double dLat, double dLon) {
//
//        String url = String.format(
//                "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f"
//                        + "?geometries=polyline&overview=full&access_token=%s",
//                oLon, oLat, dLon, dLat, MAPBOX_TOKEN
//        );
//
//        String body = Unirest.get(url).asString().getBody();
//
//        JsonObject raw = new JsonObject(body);
//
//        JsonArray routes = raw.getJsonArray("routes");
//        if (routes == null || routes.isEmpty()) {
//            return new JsonObject()
//                    .put("error", "No routes found");
//        }
//
//        JsonObject route = routes.getJsonObject(0);
//
//        String polyline = route.getString("geometry");
//        double meters = route.getDouble("distance", 0.0);
//        double seconds = route.getDouble("duration", 0.0);
//
//        double miles = meters / 1609.34;
//        double minutes = seconds / 60.0;
//
//        return new JsonObject()
//                .put("polyline", polyline)
//                .put("miles", miles)
//                .put("minutes", minutes);
//    }

