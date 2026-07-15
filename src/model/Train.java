package model;

/**
 * Represents a metro train in the Smart Metro Ticketing System.
 * A train has a unique ID, a name, and a passenger capacity.
 */
public class Train {

    // ----- Fields (private for encapsulation) -----
    private String trainId;
    private String trainName;
    private int capacity;

    // ----- Constructor -----
    public Train(String trainId, String trainName, int capacity) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.capacity = capacity;
    }

    // ----- Getters -----
    public String getTrainId() {
        return trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public int getCapacity() {
        return capacity;
    }

    // ----- Setters -----
    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Displays the train's information in a readable format.
     * Matches the "displayTrain()" method shown in the class diagram.
     */
    public void displayTrain() {
        System.out.println("Train ID   : " + trainId);
        System.out.println("Train Name : " + trainName);
        System.out.println("Capacity   : " + capacity);
    }

    /**
     * Overriding toString() so the train can be written to
     * and read back from a TXT file easily (comma-separated).
     */
    @Override
    public String toString() {
        return trainId + "," + trainName + "," + capacity;
    }
}