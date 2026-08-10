package model;

public class Route {

    private String routeId;
    private Station source;
    private Station destination;
    private double distanceKm;

    public Route(String routeId, Station source, Station destination, double distanceKm) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
    }

    public String getRouteId() {
        return routeId;
    }

    public Station getSource() {
        return source;
    }

    public Station getDestination() {
        return destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public void setSource(Station source) {
        this.source = source;
    }

    public void setDestination(Station destination) {
        this.destination = destination;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    // calc distance (need to replace?)
    public double calculateDistance() {
        return distanceKm;
    }

    public void displayRoute() {
        System.out.println("Route ID     : " + routeId);
        System.out.println("Source       : " + source.getName());
        System.out.println("Destination  : " + destination.getName());
        System.out.println("Distance(km) : " + distanceKm);
    }

    // store trains by ID
    @Override
    public String toString() {
        return routeId + "," + source.getStationId() + "," + destination.getStationId() + "," + distanceKm;
    }
}
