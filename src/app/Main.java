package app;

import service.*;
import model.*;
import repository.*;
import exception.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

/**
 * The central driving controller for the Smart Metro Ticketing System.
 * Manages the console menu presentation, routing, and data survival lifecycles.
 */
public class Main {
    // Static core application service layers
    private static StationService stationService = new StationService();
    private static TrainService trainService = new TrainService();
    private static TXTFileManager fileManager = new TXTFileManager();
    private static Scanner scanner = new Scanner(System.in);
    private static HashMap<String, String> userCredentialsMock = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println("--- Booting Smart Metro Ticketing System ---");
        
        // 1. BOOT LOAD PHASE: Restore data records from persistence files
        try {
            // Safe down-casting back to domain entity collections
            ArrayList<Station> loadedStations = (ArrayList<Station>) fileManager.loadData("stations.txt");
            ArrayList<Train> loadedTrains = (ArrayList<Train>) fileManager.loadData("trains.txt");
            
            stationService.setStations(loadedStations);
            trainService.setTrains(loadedTrains);
            
            System.out.println("[System Initialization] Local database files mapped successfully.");
        } catch (FileProcessingException e) {
            System.out.println("[Warning] File restore bypassed: " + e.getMessage());
        }

        // 2. ROOT CONSOLE LOOP
        boolean running = true;
        while (running) {
            System.out.println("\n=================================");
            System.out.println("   METRO TICKETING MAIN MENU    ");
            System.out.println("=================================");
            System.out.println("1. Login");
            System.out.println("2. Register Passenger");
            System.out.println("3. Exit & Save System Data");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handlePassengerRegistration();
                    break;
                case "3":
                    handleExitAndSave();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection! Please input 1, 2, or 3.");
            }
        }
    }

    private static void handleLogin() {
        System.out.print("Enter Registered Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Secret Password: ");
        String password = scanner.nextLine().trim();

        // 1. Check Admin Credentials
        if (email.equals("admin@metro.com") && password.equals("admin123")) {
            System.out.println("\nAuthentication Success! Logging in as System Administrator.");
            showAdminMenu();
        }

        // 2. Check Newly Registered Passengers Dynamically
        else if (userCredentialsMock.containsKey(email) && userCredentialsMock.get(email).equals(password)) {
            System.out.println("\nAuthentication Success! Logging in as Passenger.");
            showPassengerMenu();
        }

        // 3. Fallback Error Handling
        else {
            System.out.println("\n[Error] Invalid email or password sequence.");
        }
        
    }

    private static void handlePassengerRegistration() {
        System.out.println("\n--- PASSENGER REGISTRATION CARD ---");
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Target Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Create Password: ");
        String password = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("[Error] Registration failed: Fields cannot be empty.");
            return;
        }

        // Saves the credentials into our test map
        userCredentialsMock.put(email.toLowerCase(), password);
        
        // Output hook awaiting connection with Member 2's User object creation
        System.out.println("Success! Registration entry accepted for " + name + ". Please proceed to log in.");
    }

    private static void showAdminMenu() {
        boolean inAdminMenu = true;
        while (inAdminMenu) {
            System.out.println("\n=================================");
            System.out.println("        ADMIN DASHBOARD          ");
            System.out.println("=================================");
            System.out.println("1. Add Metro Station");
            System.out.println("2. View All Metro Stations");
            System.out.println("3. Add Metro Train");
            System.out.println("4. View All Metro Trains");
            System.out.println("5. Return to Main Menu (Logout)");
            System.out.print("Choose Administrative Operation: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    adminAddStationWorkflow();
                    break;
                case "2":
                    System.out.println();
                    stationService.viewStations();
                    break;
                case "3":
                    adminAddTrainWorkflow();
                    break;
                case "4":
                    System.out.println();
                    trainService.viewTrains();
                    break;
                case "5":
                    System.out.println("Exiting Admin Portal.");
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid action selection. Choose an option from 1 to 5.");
            }
        }
    }

    private static void showPassengerMenu() {
        boolean inPassengerMenu = true;
        while (inPassengerMenu) {
            System.out.println("\n=================================");
            System.out.println("       PASSENGER INTERFACE       ");
            System.out.println("=================================");
            System.out.println("1. Buy Journey Ticket");
            System.out.println("2. Top Up Wallet Balance");
            System.out.println("3. View Personal Profile");
            System.out.println("4. Return to Main Menu (Logout)");
            System.out.print("Choose Passenger Operation: ");
            
            String choice = scanner.nextLine().trim();
            if (choice.equals("4")) {
                System.out.println("Exiting Passenger Workspace.");
                inPassengerMenu = false;
            } else {
                System.out.println("\n[Feature Locked] Core logic modules are pending implementation by team members.");
            }
        }
    }

    private static void adminAddStationWorkflow() {
        System.out.println("\n--- PROMPT: NEW STATION DEPLOYMENT ---");
        System.out.print("Assign New Unique Station ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Station Name Label: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Location District: ");
        String location = scanner.nextLine().trim();

        if (id.isEmpty() || name.isEmpty() || location.isEmpty()) {
            System.out.println("[Error] Data constraint violation: Fields cannot be left empty.");
            return;
        }

        Station newStation = new Station(id, name, location);
        stationService.addStation(newStation);
    }

    private static void adminAddTrainWorkflow() {
        System.out.println("\n--- PROMPT: NEW TRAIN ACQUISITION ---");
        System.out.print("Assign New Unique Train ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Train Identification Name: ");
        String name = scanner.nextLine().trim();
        
        // Safeguarding intake to prevent numeric conversion string crashes
        int capacity = promptForSafeInteger("Specify Carriage Volume Capacity: ");

        if (id.isEmpty() || name.isEmpty()) {
            System.out.println("[Error] Data constraint violation: Fields cannot be left empty.");
            return;
        }

        Train newTrain = new Train(id, name, capacity);
        trainService.addTrain(newTrain);
    }

    private static void handleExitAndSave() {
        System.out.println("\n[System Shutdown] Initiating database text snapshot sequence...");
        try {
            fileManager.saveData(stationService.getStations(), "stations.txt");
            fileManager.saveData(trainService.getTrains(), "trains.txt");
            System.out.println("[System Shutdown] Data successfully synchronized. Safe travels!");
        } catch (FileProcessingException e) {
            System.out.println("[Critical Failure] System unable to persist internal memory states: " + e.getMessage());
        }
    }

    /**
     * Helper routine to catch non-integer user input anomalies securely.
     */
    private static int promptForSafeInteger(String promptMessage) {
        while (true) {
            System.out.print(promptMessage);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Formatting error! Input scalar digits only.");
            }
        }
    }
}