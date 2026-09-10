package service;

import model.Route;
import model.Station;

import java.util.ArrayList;

public class RouteService {

    private ArrayList<Route> routes;

    public RouteService() {
        this.routes = new ArrayList<>();
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        if(routeIdExists(route.getRouteId())){
            System.out.println("[Error] Route ID " + route.getRouteId() + " already exists. Cannot add route.");
            return;
        }
        routes.add(route);
        System.out.println("Route added successfully: " + route.getRouteId());
    }

    public boolean routeIdExists(String routeId){
        for(Route route : routes){
            if(route.getRouteId().equalsIgnoreCase(routeId)){
                return true;
            }
        }
        return false;
    }

    public Route findRoute(Station source, Station destination) {
        for (Route route : routes) {
            if (route.getSource().getStationId().equalsIgnoreCase(source.getStationId())
                    && route.getDestination().getStationId().equalsIgnoreCase(destination.getStationId())) {
                return route;
            }
        }
        return null;
    }

    public Route findRouteById(String routeId) {
        for (Route route : routes) {
            if (route.getRouteId().equalsIgnoreCase(routeId)) {
                return route;
            }
        }
        return null;
    }

    public void viewRoutes() {
        if (routes.isEmpty()) {
            System.out.println("No routes available.");
            return;
        }

        for (Route route : routes) {
            route.displayInfo();
            System.out.println("---------------------------");
        }
    }
}