package com.telio.maps;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TripShareService {

    private final Map<String, String> shareLinks = new ConcurrentHashMap<>();
    private final Map<String, double[]> sharedLocations = new ConcurrentHashMap<>();

    public String generateShareLink(String driverId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        shareLinks.put(token, driverId);
        return token;
    }

    public String getDriverIdFromToken(String token) {
        return shareLinks.get(token);
    }

    public void updateSharedLocation(String driverId, double lat, double lng) {
        sharedLocations.put(driverId, new double[]{lat, lng});
    }

    public double[] getSharedLocation(String driverId) {
        return sharedLocations.get(driverId);
    }
}
