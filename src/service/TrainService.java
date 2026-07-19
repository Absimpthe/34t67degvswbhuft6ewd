package service;

import model.Train;
import java.util.ArrayList;

/**
 * Handles all operations related to Train objects:
 * adding trains and viewing all trains.
 * Trains are stored in memory using an ArrayList.
 */
public class TrainService {

    // ----- Field -----
    // Matches the class diagram: "-ArrayList<Train> trains"
    private ArrayList<Train> trains;

    // ----- Constructor -----
    public TrainService() {
        this.trains = new ArrayList<>();
    }

    /**
     * Adds a new train to the list.
     * Matches "+addTrain(train) : void" in the class diagram.
     */
    public void addTrain(Train train) {
        trains.add(train);
        System.out.println("Train added successfully: " + train.getTrainName());
    }

    /**
     * Displays all trains currently stored.
     * Matches "+viewTrains() : void" in the class diagram.
     */
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

    /**
     * Gives other classes (e.g. FileManager) access
     * to the full list of trains when needed.
     */
    public ArrayList<Train> getTrains() {
        return trains;
    }
}