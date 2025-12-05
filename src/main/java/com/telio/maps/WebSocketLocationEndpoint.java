package com.telio.maps;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.inject.Inject;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/ws/location")
public class WebSocketLocationEndpoint {

    @Inject
    RealTimeLocationService locationService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            LocationMessage msg = mapper.readValue(message, LocationMessage.class);
            locationService.updateDriverLocation(msg.driverId, msg.lat, msg.lng);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class LocationMessage {
        public String driverId;
        public double lat;
        public double lng;
    }
}

