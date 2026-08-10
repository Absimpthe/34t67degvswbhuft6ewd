package model;

public class Train {

    private String trainId;
    private String trainName;
    private int capacity;

    public Train(String trainId, String trainName, int capacity) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.capacity = capacity;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void displayTrain() {
        System.out.println("Train ID   : " + trainId);
        System.out.println("Train Name : " + trainName);
        System.out.println("Capacity   : " + capacity);
    }

    @Override
    public String toString() {
        return trainId + "," + trainName + "," + capacity;
    }
}