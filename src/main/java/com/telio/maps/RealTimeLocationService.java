package com.telio.maps;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class RealTimeLocationService {

    private final Map<String, double[]> driverLocations = new ConcurrentHashMap<>();
    private final Map<String, DriverState> driverStates = new ConcurrentHashMap<>();

    public void updateDriverLocation(String driverId, double lat, double lng) {
        driverLocations.put(driverId, new double[]{lat, lng});
    }

    public double[] getDriverLocation(String driverId) {
        return driverLocations.get(driverId);
    }

    public void updateDriverState(String driverId, DriverState state) {
        driverStates.put(driverId, state);
    }

    public DriverState getDriverState(String driverId) {
        return driverStates.getOrDefault(driverId, DriverState.OFFLINE);
    }
}
