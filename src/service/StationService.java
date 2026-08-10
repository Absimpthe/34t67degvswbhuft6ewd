package service;

import model.Station;
import java.util.ArrayList;

public class StationService {

    // needed to store stations
    private ArrayList<Station> stations;

    // initialize new list (replace later with file)
    public StationService() {
        this.stations = new ArrayList<>();
    }

    // appends station to the end of the list
    public void addStation(Station station) {
        stations.add(station);
        System.out.println("Station added successfully: " + station.getName());
    }

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

    // searches for station by name (case-insensitive), returns null if not found
    public Station searchStation(String name) {
        for (Station station : stations) {
            if (station.getName().equalsIgnoreCase(name)) {
                return station;
            }
        }
        return null;
    }

    // for giving other classes access to list of stations
    public ArrayList<Station> getStations() {
        return stations;
    }
}