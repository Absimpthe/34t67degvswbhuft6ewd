package service;

import model.Route;
import model.Station;
import java.util.ArrayList;

/**
 * Handles all operations related to Route objects:
 * creating routes, viewing all routes, and finding a route
 * between a given source and destination station.
 * Routes are stored in memory using an ArrayList.
 */
public class RouteService {

    // ----- Field -----
    // Matches the class diagram: "-ArrayList<Route> routes"
    private ArrayList<Route> routes;

    // ----- Constructor -----
    public RouteService() {
        this.routes = new ArrayList<>();
    }

    /**
     * Adds a new route to the list.
     * Matches "+addRoute(route) : void" in the class diagram.
     */
    public void addRoute(Route route) {
        routes.add(route);
        System.out.println("Route added successfully: "
                + route.getSource().getName() + " -> " + route.getDestination().getName());
    }

    /**
     * Displays all routes currently stored.
     */
    public void viewRoutes() {
        if (routes.isEmpty()) {
            System.out.println("No routes available.");
            return;
        }

        System.out.println("----- List of Routes -----");
        for (Route route : routes) {
            route.displayRoute();
            System.out.println("---------------------------");
        }
    }

    /**
     * Finds a route that matches the given source and destination stations.
     * Matches "+findRoute(source, destination) : Route" in the class diagram.
     *
     * @param source      the source station
     * @param destination the destination station
     * @return the matching Route, or null if no route is found
     */
    public Route findRoute(Station source, Station destination) {
        for (Route route : routes) {
            boolean sameSource = route.getSource().getStationId().equals(source.getStationId());
            boolean sameDestination = route.getDestination().getStationId().equals(destination.getStationId());

            if (sameSource && sameDestination) {
                return route;
            }
        }
        return null;
    }

    /**
     * Gives other classes (e.g. FileManager) access
     * to the full list of routes when needed.
     */
    public ArrayList<Route> getRoutes() {
        return routes;
    }
}