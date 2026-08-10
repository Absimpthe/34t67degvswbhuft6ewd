package service;

import model.Route;
import model.Station;
import java.util.ArrayList;

public class RouteService {

    private ArrayList<Route> routes;

    public RouteService() {
        this.routes = new ArrayList<>();
    }

    public void addRoute(Route route) {
        routes.add(route);
        System.out.println("Route added successfully: "
                + route.getSource().getName() + " -> " + route.getDestination().getName());
    }

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

    // finds route based on source and destination stations, checks if they match query
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

    public ArrayList<Route> getRoutes() {
        return routes;
    }
}