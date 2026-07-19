package service;

import model.Station;
import java.util.ArrayList;

/**
 * Handles all operations related to Station objects:
 * adding stations, viewing all stations, and searching by name.
 * Stations are stored in memory using an ArrayList.
 */
public class StationService {

    // ----- Field -----
    // Matches the class diagram: "-ArrayList<Station> stations"
    private ArrayList<Station> stations;

    // ----- Constructor -----
    public StationService() {
        this.stations = new ArrayList<>();
    }

    /**
     * Adds a new station to the list.
     * Matches "+addStation(station) : void" in the class diagram.
     */
    public void addStation(Station station) {
        stations.add(station);
        System.out.println("Station added successfully: " + station.getName());
    }

    /**
     * Displays all stations currently stored.
     * Matches "+viewStations() : void" in the class diagram.
     */
    public void viewStations() {
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }

        System.out.println("----- List of Stations -----");
        for (Station station : stations) {
            station.displayInfo();
            System.out.println("-----------------------------");
        }
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    /**
     * Searches for a station by name (case-insensitive).
     * Matches "+searchStation(name) : Station" in the class diagram.
     *
     * @param name the station name to search for
     * @return the matching Station, or null if not found
     */
    public Station searchStation(String name) {
        for (Station station : stations) {
            if (station.getName().equalsIgnoreCase(name)) {
                return station;
            }
        }
        return null;
    }

    /**
     * Gives other classes (e.g. RouteService, FileManager) access
     * to the full list of stations when needed.
     */
    public ArrayList<Station> getStations() {
        return stations;
    }
}