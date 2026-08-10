package service;

import model.Train;
import java.util.ArrayList;

public class TrainService {

    private ArrayList<Train> trains;

    public TrainService() {
        this.trains = new ArrayList<>();
    }

    public void addTrain(Train train) {
        trains.add(train);
        System.out.println("Train added successfully: " + train.getTrainName());
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