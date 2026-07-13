package model;

public class Station {
	private String stationId;
	private String name;
	private String location;
	
	public Station(String stationId, String name, String location) {
	    this.stationId = stationId;
	    this.name = name;
	    this.location = location;
	}
	
	public String getStationId() {
        return stationId;
    }
 
    public String getName() {
        return name;
    }
 
    public String getLocation() {
        return location;
    }
 
    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setLocation(String location) {
        this.location = location;
    }

    public void displayInfo() {
        System.out.println("Station ID : " + stationId);
        System.out.println("Name       : " + name);
        System.out.println("Location   : " + location);
    }

    // Overriding toString() so the station can be printed or written to file easily
    @Override
    public String toString() {
        return stationId + "," + name + "," + location;
    }
}
