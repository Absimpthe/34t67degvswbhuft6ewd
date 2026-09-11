package service;

import model.Train;
import java.util.ArrayList;

public class TrainService {

    private ArrayList<Train> trains;

    public TrainService() {
        this.trains = new ArrayList<>();
    }

    public boolean addTrain(Train train) {
        if(trainIdExists(train.getTrainId())){
            System.out.println("[Error] Train ID " + train.getTrainId() + " already exists. IDs must be unique.");
            return false;
        }
        trains.add(train);
        System.out.println("Train added successfully: " + train.getTrainName());
        return true;
    }

    public boolean trainIdExists(String trainId){
        for(Train train : trains){
            if(train.getTrainId().equalsIgnoreCase(trainId)){
                return true;
            }
        }
        return false;
    }
    public void viewTrains() {
        if (trains.isEmpty()) {
            System.out.println("No trains available.");
            return;
        }

        System.out.println("----- List of Trains -----");
        for (Train train : trains) {
            train.displayTrain();
            System.out.println("---------------------------");
        }
    }

    public void setTrains(ArrayList<Train> trains) {
        this.trains = trains;
    }

    // give other classes access to list of trains
    public ArrayList<Train> getTrains() {
        return trains;
    }
}