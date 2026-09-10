package service;

import model.Station;
import java.util.ArrayList;

public class StationService {

    // needed to store stations
    private ArrayList<Station> stations;

    // initialize new list
    public StationService() {
        this.stations = new ArrayList<>();
    }

    // appends station to the end of the list
    public boolean addStation(Station station) {
        if(stationIdExists(station.getStationId())){
            System.out.println("[Error] Station ID " + station.getStationId() + " already exists. Cannot add station.");
            return false;
        }
        stations.add(station);
        System.out.println("Station added successfully: " + station.getName());
        return true;
    }

    public boolean stationIdExists(String stationId){
        String searchId = (stationId == null) ? " " : stationId.trim();
        for(Station station : stations){
            String currentId = station.getStationId();
            if(currentId != null && currentId.trim().equalsIgnoreCase(searchId)){
                return true;
            }
        }
        return false;
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