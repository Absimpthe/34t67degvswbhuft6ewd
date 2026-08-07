package model;

/**
 * Represents a route between two metro stations.
 * A route has a unique ID, a source station, a destination station,
 * and the distance (in kilometers) between them.
 */
public class Route {

    // ----- Fields (private for encapsulation) -----
    private String routeId;
    private Station source;
    private Station destination;
    private double distanceKm;

    // ----- Constructor -----
    public Route(String routeId, Station source, Station destination, double distanceKm) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
    }

    // ----- Getters -----
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

    // ----- Setters -----
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

    /**
     * Calculates the distance of this route.
     * Matches "+calculateDistance() : double" in the class diagram.
     * (Distance is already stored directly, so this simply returns it;
     * groups could expand this later, e.g. summing distances across
     * multiple linked stations for more complex route networks.)
     */
    public double calculateDistance() {
        return distanceKm;
    }

    /**
     * Displays the route's information in a readable format.
     * Matches the "displayRoute()" method shown in the class diagram.
     */
    public void displayRoute() {
        System.out.println("Route ID     : " + routeId);
        System.out.println("Source       : " + source.getName());
        System.out.println("Destination  : " + destination.getName());
        System.out.println("Distance(km) : " + distanceKm);
    }

    /**
     * Overriding toString() so the route can be written to
     * and read back from a TXT file easily (comma-separated).
     * Stations are stored by their station ID, not the full object,
     * since the object itself can't be written directly to a text file.
     */
    @Override
    public String toString() {
        return routeId + "," + source.getStationId() + "," + destination.getStationId() + "," + distanceKm;
    }
}
