package com.telio.maps;

//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import io.vertx.core.json.JsonObject;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import jakarta.json.Json;
//import jakarta.json.JsonArray;
//import jakarta.json.JsonObject;
//
//import java.io.StringReader;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@ApplicationScoped
public class MapsService {

    @Inject
    MapboxClient mapboxClient;

    @Inject
    RealTimeLocationService locationService;

    @Inject
    TripShareService shareService;

    // ------------------------------------------------------
    // 🟦 CALCULAR RUTA (Mapbox)
    // ------------------------------------------------------
    public JsonObject calculateRoute(double oLat, double oLng,
                                     double dLat, double dLng) {
        return (JsonObject) mapboxClient.getRoute(oLat, oLng, dLat, dLng);
    }

    public JsonObject computeRoute(JsonObject body) {

        JsonArray coords = body.getJsonArray("coordinates");
        if (coords == null || coords.isEmpty()) {
            return Json.createObjectBuilder()
                    .add("error", "Missing coordinates array")
                    .build();
        }

        JsonObject origin = coords.getJsonObject(0);
        JsonObject dest = coords.getJsonObject(1);

        double oLng = origin.getJsonNumber("lng").doubleValue();
        double oLat = origin.getJsonNumber("lat").doubleValue();
        double dLng = dest.getJsonNumber("lng").doubleValue();
        double dLat = dest.getJsonNumber("lat").doubleValue();

        // 🔥 VALIDACIÓN BÁSICA
        if (Math.abs(oLat) > 90 || Math.abs(dLat) > 90) {
            return Json.createObjectBuilder()
                    .add("error", "Invalid latitude detected")
                    .build();
        }

        String coordsUrl = oLng + "," + oLat + ";" + dLng + "," + dLat;

        String url = "https://api.mapbox.com/directions/v5/mapbox/driving/"
                + coordsUrl
                + "?geometries=polyline&overview=full&access_token=pk.eyJ1IjoidGVsaW9yaWRlcyIsImEiOiJjbWZxdW11dngwMm90MmxwenU5emNhMHN0In0.g_d23QobmXqFyUElkDl5ag";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String json = res.body();

            // Log para ver qué pasa
            System.out.println("MAPBOX RAW → " + json);

            JsonObject mapbox = Json.createReader(new StringReader(json)).readObject();

            JsonArray routes = mapbox.getJsonArray("routes");
            if (routes == null || routes.isEmpty()) {
                return Json.createObjectBuilder()
                        .add("error", "Mapbox returned no routes")
                        .build();
            }

            JsonObject firstRoute = routes.getJsonObject(0);

            String poly = firstRoute.getString("geometry", null);
            if (poly == null) {
                return Json.createObjectBuilder()
                        .add("error", "No polyline in Mapbox response")
                        .build();
            }

            double distanceM = firstRoute.getJsonNumber("distance").doubleValue();
            double durationS = firstRoute.getJsonNumber("duration").doubleValue();

            return Json.createObjectBuilder()
                    .add("polyline", poly)
                    .add("miles", distanceM / 1609.34)
                    .add("minutes", durationS / 60)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
        }
    }



    // ------------------------------------------------------
    // 🟦 DRIVER LOCATION (WS + HTTP)
    // ------------------------------------------------------
    public void updateDriverLocation(String driverId, double lat, double lng) {
        locationService.updateDriverLocation(driverId, lat, lng);
    }

    public double[] getDriverLocation(String driverId) {
        return locationService.getDriverLocation(driverId);
    }

    // ------------------------------------------------------
    // 🟦 ESTADO DEL DRIVER
    // ------------------------------------------------------
    public void updateDriverState(String driverId, DriverState state) {
        locationService.updateDriverState(driverId, state);
    }

    public DriverState getDriverState(String driverId) {
        return locationService.getDriverState(driverId);
    }

    // ------------------------------------------------------
    // 🟦 LINK DE SEGUIMIENTO EN VIVO
    // ------------------------------------------------------
    public String generateShareLink(String driverId) {
        return shareService.generateShareLink(driverId);
    }

    public double[] getSharedLocation(String token) {
        String driverId = shareService.getDriverIdFromToken(token);
        return shareService.getSharedLocation(driverId);
    }

    // ------------------------------------------------------
    // 🟦 ACTUALIZAR UBICACIÓN PÚBLICA (para share.html)
    // ------------------------------------------------------
    public void updateSharedLocation(String driverId, double lat, double lng) {
        shareService.updateSharedLocation(driverId, lat, lng);
    }

    // ------------------------------------------------------
    // 🟦 ALERTA DE PROXIMIDAD
    // ------------------------------------------------------
    public boolean isNearDestination(double lat, double lng,
                                     double goalLat, double goalLng) {
        double distKm = DistanceUtil.distanceKm(lat, lng, goalLat, goalLng);
        return distKm <= 0.15;  // 150 metros
    }
}



//package com.telio.maps;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import jakarta.json.JsonObject;
//
//import java.util.Map;
//
//@ApplicationScoped
//public class MapsService {
//
//    @Inject
//    MapboxClient mapboxClient;
//
//    @Inject
//    RealTimeLocationService locationService;
//
//    @Inject
//    TripShareService shareService;
//
//    public JsonObject calculateRoute(double oLat, double oLng,
//                                              double dLat, double dLng) {
//        return mapboxClient.getRoute(oLat, oLng, dLat, dLng);
//    }
//
//    public void updateDriverLocation(String driverId, double lat, double lng) {
//        locationService.updateDriverLocation(driverId, lat, lng);
//    }
//
//    public double[] getDriverLocation(String driverId) {
//        return locationService.getDriverLocation(driverId);
//    }
//
//    public void updateDriverState(String driverId, DriverState state) {
//        locationService.updateDriverState(driverId, state);
//    }
//
//    public DriverState getDriverState(String driverId) {
//        return locationService.getDriverState(driverId);
//    }
//
//    public String generateShareLink(String driverId) {
//        return shareService.generateShareLink(driverId);
//    }
//
//    public double[] getSharedLocation(String token) {
//        String driverId = shareService.getDriverIdFromToken(token);
//        return shareService.getSharedLocation(driverId);
//    }
//
//    public boolean isNearDestination(double lat, double lng,
//                                     double goalLat, double goalLng) {
//        double distKm = DistanceUtil.distanceKm(lat, lng, goalLat, goalLng);
//        return distKm <= 0.15; // 150 metros
//    }
//}



//////package com.telio.maps;
//////
//////public class MapsService {
//////}
////
////
//package com.telio.maps;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import jakarta.json.Json;
//import jakarta.json.JsonArray;
//import jakarta.json.JsonObject;
//
//import java.io.StringReader;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
////
////@ApplicationScoped
////public class MapsService {
////
////    @Inject
////    MapboxClient client;
////
//    public JsonObject computeRoute(JsonObject req) {
//        System.out.println("-----------  2");
//
//        JsonObject o = req.getJsonObject("origin");
//        JsonObject d = req.getJsonObject("destination");
//
//        double oLat = o.getJsonNumber("lat").doubleValue();
//        double oLon = o.getJsonNumber("lon").doubleValue();
//        double dLat = d.getJsonNumber("lat").doubleValue();
//        double dLon = d.getJsonNumber("lon").doubleValue();
//
//        JsonObject mapbox = client.getRoute(oLat, oLon, dLat, dLon);
//        System.out.println("-----------  3.1");
//        JsonObject route = mapbox.getJsonArray("route").getJsonObject(0);
//        System.out.println("-----------  4");
//        double distanceMiles = route.getJsonNumber("distance").doubleValue() / 1609.34;
//        double durationMin = route.getJsonNumber("duration").doubleValue() / 60;
//        String polyline = route.getString("geometry");
//
//        return Json.createObjectBuilder()
//                .add("miles", distanceMiles)
//                .add("minutes", durationMin)
//                .add("polyline", polyline)
//                .build();
//    }
////}
//@ApplicationScoped
//public class MapsService {
//
//    public JsonObject computeRoute(JsonObject body) {
//
//        JsonArray coords = body.getJsonArray("coordinates");
//        if (coords == null || coords.isEmpty()) {
//            return Json.createObjectBuilder()
//                    .add("error", "Missing coordinates array")
//                    .build();
//        }
//
//        JsonObject origin = coords.getJsonObject(0);
//        JsonObject dest = coords.getJsonObject(1);
//
//        double oLng = origin.getJsonNumber("lng").doubleValue();
//        double oLat = origin.getJsonNumber("lat").doubleValue();
//        double dLng = dest.getJsonNumber("lng").doubleValue();
//        double dLat = dest.getJsonNumber("lat").doubleValue();
//
//        // 🔥 VALIDACIÓN BÁSICA
//        if (Math.abs(oLat) > 90 || Math.abs(dLat) > 90) {
//            return Json.createObjectBuilder()
//                    .add("error", "Invalid latitude detected")
//                    .build();
//        }
//
//        String coordsUrl = oLng + "," + oLat + ";" + dLng + "," + dLat;
//
//        String url = "https://api.mapbox.com/directions/v5/mapbox/driving/"
//                + coordsUrl
//                + "?geometries=polyline&overview=full&access_token=pk.eyJ1IjoidGVsaW9yaWRlcyIsImEiOiJjbWZxdW11dngwMm90MmxwenU5emNhMHN0In0.g_d23QobmXqFyUElkDl5ag";
//
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//            HttpRequest req = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .GET()
//                    .build();
//
//            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
//            String json = res.body();
//
//            // ⛳ Log para ver qué pasa
//            System.out.println("MAPBOX RAW → " + json);
//
//            JsonObject mapbox = Json.createReader(new StringReader(json)).readObject();
//
//            JsonArray routes = mapbox.getJsonArray("routes");
//            if (routes == null || routes.isEmpty()) {
//                return Json.createObjectBuilder()
//                        .add("error", "Mapbox returned no routes")
//                        .build();
//            }
//
//            JsonObject firstRoute = routes.getJsonObject(0);
//
//            String poly = firstRoute.getString("geometry", null);
//            if (poly == null) {
//                return Json.createObjectBuilder()
//                        .add("error", "No polyline in Mapbox response")
//                        .build();
//            }
//
//            double distanceM = firstRoute.getJsonNumber("distance").doubleValue();
//            double durationS = firstRoute.getJsonNumber("duration").doubleValue();
//
//            return Json.createObjectBuilder()
//                    .add("polyline", poly)
//                    .add("miles", distanceM / 1609.34)
//                    .add("minutes", durationS / 60)
//                    .build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Json.createObjectBuilder()
//                    .add("error", e.getMessage())
//                    .build();
//        }
//    }
//}
