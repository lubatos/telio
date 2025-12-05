package com.telio.maps;

//import io.vertx.core.json.JsonObject;
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;


@Path("/api/maps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MapsController {

    @Inject
    MapsService mapsService;

    // -------------------------------
    // 🔵 RECORDS (Request DTOs)
    // -------------------------------
    record RouteRequest(Coordinate[] coordinates) {}
    record Coordinate(double lat, double lng) {}

    record LocationUpdate(String driverId, double lat, double lng) {}
    record ShareResponse(String shareUrl) {}

    record PublicLocationUpdate(String driverId, double lat, double lng) {}
    record NearResponse(boolean near) {}

    // -------------------------------
    // 🔵 CALCULAR RUTA
    // -------------------------------
//    @POST
//    @Path("/route")
//    public JsonObject getRoute(RouteRequest req) {
//        System.out.println("LLEGO AQUI");
//        Coordinate o = req.coordinates()[0];
//        Coordinate d = req.coordinates()[1];
//        System.out.println("Calculo");
//        return mapsService.calculateRoute(o.lat(), o.lng(), d.lat(), d.lng());
//    }
    @POST
    @Path("/route")
    public Response route(JsonObject body) {
        System.out.println("-----------  1"+body.toString());
        JsonObject result = mapsService.computeRoute(body);
        return Response.ok(result).build();
    }

    // -------------------------------
    // 🔵 GUARDAR UBICACIÓN DEL DRIVER (WS o HTTP)
    // -------------------------------
    @POST
    @Path("/driver/location")
    public void updateDriverLocation(LocationUpdate req) {
        mapsService.updateDriverLocation(req.driverId(), req.lat(), req.lng());
    }

    // -------------------------------
    // 🔵 OBTENER UBICACIÓN DEL DRIVER (usado por rider.html)
    // -------------------------------
    @GET
    @Path("/driver/location/{driverId}")
    public double[] getDriverLocation(@PathParam("driverId") String driverId) {
        double[] pos = mapsService.getDriverLocation(driverId);
        if (pos == null) {
            throw new NotFoundException("Driver location not found");
        }
        return pos;
    }

    // -------------------------------
    // 🔵 ESTADO DEL DRIVER
    // -------------------------------
    @POST
    @Path("/driver/state/{driverId}/{state}")
    public void updateState(
            @PathParam("driverId") String driverId,
            @PathParam("state") String state
    ) {
        mapsService.updateDriverState(driverId, DriverState.valueOf(state));
    }

    @GET
    @Path("/driver/state/{driverId}")
    public DriverState getState(@PathParam("driverId") String driverId) {
        return mapsService.getDriverState(driverId);
    }

    // -------------------------------
    // 🔵 GENERAR LINK DE VIAJE EN VIVO
    // -------------------------------
    @POST
    @Path("/share/{driverId}")
    public ShareResponse createShare(@PathParam("driverId") String driverId) {
        String token = mapsService.generateShareLink(driverId);
        return new ShareResponse("https://telio.app/tracking?t=" + token);
    }

    // -------------------------------
    // 🔵 OBTENER UBICACIÓN PUBLICA POR TOKEN (share.html)
    // -------------------------------
    @GET
    @Path("/share/loc/{token}")
    public double[] getSharedLocation(@PathParam("token") String token) {
        double[] loc = mapsService.getSharedLocation(token);
        if (loc == null) {
            throw new NotFoundException("Shared location not found");
        }
        return loc;
    }

    // -------------------------------
    // 🔵 ALERTA DE CERCANÍA AL DESTINO
    // /near?lat=...&lng=...&goalLat=...&goalLng=...
    // -------------------------------
    @GET
    @Path("/near")
    public NearResponse getNearAlert(
            @QueryParam("lat") double lat,
            @QueryParam("lng") double lng,
            @QueryParam("goalLat") double goalLat,
            @QueryParam("goalLng") double goalLng
    ) {
        boolean near = mapsService.isNearDestination(lat, lng, goalLat, goalLng);
        return new NearResponse(near);
    }

    // -------------------------------
    // 🔵 (Opcional) ACTUALIZAR UBICACIÓN PÚBLICA MANUALMENTE
    // -------------------------------
    @POST
    @Path("/share/update")
    public void updatePublicLocation(PublicLocationUpdate req) {
        mapsService.updateSharedLocation(req.driverId(), req.lat(), req.lng());
    }
}





//package com.telio.maps;
//
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import java.util.Map;
//
//@Path("/api/maps")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//public class MapsController {
//
//    @Inject
//    MapsService mapsService;
//
//    record RouteRequest(Coordinate[] coordinates) {}
//    record Coordinate(double lat, double lng) {}
//
//    record LocationUpdate(String driverId, double lat, double lng) {}
//    record ShareResponse(String shareUrl) {}
//
//    @POST
//    @Path("/route")
//    public Map<String, Object> getRoute(RouteRequest req) {
//        Coordinate o = req.coordinates()[0];
//        Coordinate d = req.coordinates()[1];
//        return mapsService.calculateRoute(o.lat(), o.lng(), d.lat(), d.lng());
//    }
//
//    @POST
//    @Path("/driver/location")
//    public void updateDriverLocation(LocationUpdate req) {
//        mapsService.updateDriverLocation(req.driverId(), req.lat(), req.lng());
//    }
//
//    @POST
//    @Path("/driver/state/{driverId}/{state}")
//    public void updateState(@PathParam("driverId") String driverId,
//                            @PathParam("state") String state) {
//        mapsService.updateDriverState(driverId, DriverState.valueOf(state));
//    }
//
//    @GET
//    @Path("/driver/state/{driverId}")
//    public DriverState getState(@PathParam("driverId") String driverId) {
//        return mapsService.getDriverState(driverId);
//    }
//
//    @POST
//    @Path("/share/{driverId}")
//    public ShareResponse createShare(@PathParam("driverId") String driverId) {
//        String token = mapsService.generateShareLink(driverId);
//        return new ShareResponse("https://telio.app/tracking/" + token);
//    }
//}
//
//
//
//////package com.telio.maps;
//////
//////public class MapsController {
//////}
////
////package com.telio.maps;
////
////import jakarta.inject.Inject;
////import jakarta.ws.rs.*;
////import jakarta.ws.rs.core.Response;
////import jakarta.json.JsonObject;
////import jakarta.ws.rs.core.MediaType;
////
////@Path("/api/maps")
////@Consumes(MediaType.APPLICATION_JSON)
////@Produces(MediaType.APPLICATION_JSON)
////public class MapsController {
////
////    @Inject
////    MapsService service;
////
////    @POST
////    @Path("/route")
////    public Response route(JsonObject body) {
////        System.out.println("-----------  1");
////        JsonObject result = service.computeRoute(body);
////        return Response.ok(result).build();
////    }
////
////    @POST
////    @Path("/trip/share")
////    @Produces(MediaType.APPLICATION_JSON)
////    public Response createShareLink(LiveShareRequest req) {
////
////        String token = UUID.randomUUID().toString();
////
////        // Guardar en BD
////        LiveShareEntity entity = new LiveShareEntity();
////        entity.setTripId(req.getTripId());
////        entity.setToken(token);
////        entity.setExpiresAt(Instant.now().plus(Duration.ofHours(2)));
////        liveShareRepo.persist(entity);
////
////        String url = "https://telio.app/live/" + token;
////
////        return Response.ok(new ShareLinkResponse(url)).build();
////    }
////
////    @ServerEndpoint("/ws/live/{token}")
////    public class LiveShareSocket {
////
////        @OnOpen
////        public void open(Session session, @PathParam("token") String token) {
////            if (!isTokenValid(token)) {
////                session.close();
////                return;
////            }
////        }
////
////        // el backend reenvía la ubicación del driver hacia este canal público
////    }
////
////
////}
