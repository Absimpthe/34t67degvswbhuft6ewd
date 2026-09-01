package app;

import service.UserService;
import service.StationService;
import service.TrainService;
import service.RouteService;
import service.TicketService;
import service.PaymentService;
import service.ReportService;
import model.Admin;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import repository.TXTFileManager;
import exception.FileProcessingException;
import exception.InvalidLoginException;
import exception.TicketNotFoundException;
import payment.CardPayment;
import payment.CashPayment;
import payment.Payment;
import enums.TicketType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Main {

    private static final String USERS_FILE = "users.txt";
    private static final String STATIONS_FILE = "stations.txt";
    private static final String TRAINS_FILE = "trains.txt";
    private static final String ROUTES_FILE = "routes.txt";
    private static final String TICKETS_FILE = "tickets.txt";

    private static final String DEFAULT_ADMIN_ID = "AD000";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@metro.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private static UserService userService = new UserService();
    private static StationService stationService = new StationService();
    private static TrainService trainService = new TrainService();
    private static RouteService routeService = new RouteService();
    private static TicketService ticketService = new TicketService();
    private static PaymentService paymentService = new PaymentService();
    private static ReportService reportService = new ReportService();
    private static TXTFileManager fileManager = new TXTFileManager();

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("--- Booting Smart Metro Ticketing System ---");
        loadAllData();

        boolean running = true;
        while (running) {
            System.out.println("\n=================================");
            System.out.println("   METRO TICKETING MAIN MENU    ");
            System.out.println("=================================");
            System.out.println("1. Register Passenger");
            System.out.println("2. Login");
            System.out.println("3. Exit & Save System Data");
            System.out.print("Enter choice: ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    handlePassengerRegistration();
                    break;
                case "2":
                    handleLogin();
                    break;
                case "3":
                    saveAllData();
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection! Please input 1, 2, or 3.");
            }
        }
        scanner.close();
    }

    // ---------------- data loading / saving ----------------

    private static void loadAllData() {
        try {
            ArrayList<Station> loadedStations = castStations(fileManager.loadData(STATIONS_FILE));
            ArrayList<Train> loadedTrains = castTrains(fileManager.loadData(TRAINS_FILE));
            stationService.setStations(loadedStations);
            trainService.setTrains(loadedTrains);

            for (User u : fileManager.loadUsers(USERS_FILE)) {
                userService.register(u);
            }

            routeService.getRoutes().addAll(fileManager.loadRoutes(ROUTES_FILE, stationService.getStations()));

            ticketService.setTickets(fileManager.loadTickets(TICKETS_FILE, userService.getUsers(), routeService.getRoutes()));

            System.out.println("[System Initialization] Local database files mapped successfully.");
        } catch (FileProcessingException e) {
            System.out.println("[Warning] File restore bypassed: " + e.getMessage());
        }

        // guarantee there is always a single admin account to log in with
        if (userService.findUser(DEFAULT_ADMIN_ID) == null) {
            userService.register(new Admin(DEFAULT_ADMIN_ID, "System Admin", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD));
            try {
                fileManager.saveData(new ArrayList<User>(userService.getUsers().values()), USERS_FILE);
            } catch (FileProcessingException e) {
                System.out.println("[Warning] Could not save default admin immediately: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Station> castStations(Object data) {
        return (ArrayList<Station>) data;
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Train> castTrains(Object data) {
        return (ArrayList<Train>) data;
    }

    private static void saveAllData() {
        System.out.println("\n[System Shutdown] Initiating database text snapshot sequence...");
        try {
            fileManager.saveData(new ArrayList<User>(userService.getUsers().values()), USERS_FILE);
            fileManager.saveData(stationService.getStations(), STATIONS_FILE);
            fileManager.saveData(trainService.getTrains(), TRAINS_FILE);
            fileManager.saveData(routeService.getRoutes(), ROUTES_FILE);
            fileManager.saveData(ticketService.getTickets(), TICKETS_FILE);
            System.out.println("[System Shutdown] Data successfully synchronized. Safe travels!");
        } catch (FileProcessingException e) {
            System.out.println("[Critical Failure] System unable to persist internal memory states: " + e.getMessage());
        }
    }

    // ---------------- authentication ----------------

    private static void handleLogin() {
        String email = promptForValidEmail("Enter Email: ");
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = userService.login(email, password);
            System.out.println("\nAuthentication Success! Welcome, " + user.getName() + ".");

            if (user instanceof Admin) {
                showAdminMenu((Admin) user);
            } else if (user instanceof Passenger) {
                showPassengerMenu((Passenger) user);
            }
        } catch (InvalidLoginException e) {
            System.out.println("\n[Error] " + e.getMessage());
        }
    }

    private static void handlePassengerRegistration() {
        System.out.println("\n--- PASSENGER REGISTRATION ---");
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        String email = promptForValidEmail("Enter Email: ");
        System.out.print("Create Password: ");
        String password = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("[Error] Registration failed: Fields cannot be empty.");
            return;
        }

        if (userService.findUserByEmail(email) != null) {
            System.out.println("[Error] Registration failed: Email is already registered.");
            return;
        }

        String userId = userService.generatePassengerId();
        userService.register(new Passenger(userId, name, email, password, 0.0));
        System.out.println("Registration successful! Your User ID is " + userId + ". You may now log in as " + name + ".");
    }

    // ---------------- admin menu ----------------

    private static void showAdminMenu(Admin admin) {
        boolean inAdminMenu = true;
        while (inAdminMenu) {
            System.out.println("\n=================================");
            System.out.println("   ADMIN DASHBOARD - " + admin.getName());
            System.out.println("=================================");
            System.out.println("1. Add Station");
            System.out.println("2. View Stations");
            System.out.println("3. Search Station");
            System.out.println("4. Sort Stations by Name");
            System.out.println("5. Add Train");
            System.out.println("6. View Trains");
            System.out.println("7. Create Route");
            System.out.println("8. View Routes");
            System.out.println("9. View All Tickets");
            System.out.println("10. Generate Report");
            System.out.println("11. Logout");
            System.out.print("Choose Administrative Operation: ");

            switch (scanner.nextLine().trim()) {
                case "1": adminAddStation(); break;
                case "2": stationService.viewStations(); break;
                case "3": adminSearchStation(); break;
                case "4": adminSortStations(); break;
                case "5": adminAddTrain(); break;
                case "6": trainService.viewTrains(); break;
                case "7": adminCreateRoute(); break;
                case "8": routeService.viewRoutes(); break;
                case "9": ticketService.viewAllTickets(); break;
                case "10": reportService.generateReport(ticketService.getTickets()); break;
                case "11":
                    System.out.println("Logging out of Admin Portal.");
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid action selection. Choose an option from 1 to 11.");
            }
        }
    }

    private static void adminAddStation() {
        System.out.println("\n--- ADD STATION ---");
        System.out.print("Station ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Station Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Location: ");
        String location = scanner.nextLine().trim();

        if (id.isEmpty() || name.isEmpty() || location.isEmpty()) {
            System.out.println("[Error] Fields cannot be left empty.");
            return;
        }
        stationService.addStation(new Station(id, name, location));
    }

    private static void adminSearchStation() {
        System.out.print("\nEnter Station Name to Search: ");
        String name = scanner.nextLine().trim();
        Station found = stationService.searchStation(name);
        if (found == null) {
            System.out.println("[Error] No station found matching '" + name + "'.");
        } else {
            found.displayInfo();
        }
    }

    private static void adminSortStations() {
        ArrayList<Station> stations = stationService.getStations();
        if (stations.isEmpty()) {
            System.out.println("No stations available to sort.");
            return;
        }
        Collections.sort(stations, Comparator.comparing(Station::getName));
        System.out.println("Stations sorted alphabetically by name:");
        stationService.viewStations();
    }

    private static void adminAddTrain() {
        System.out.println("\n--- ADD TRAIN ---");
        System.out.print("Train ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Train Name: ");
        String name = scanner.nextLine().trim();
        int capacity = promptForSafeInteger("Capacity: ");

        if (id.isEmpty() || name.isEmpty()) {
            System.out.println("[Error] Fields cannot be left empty.");
            return;
        }
        trainService.addTrain(new Train(id, name, capacity));
    }

    private static void adminCreateRoute() {
        System.out.println("\n--- CREATE ROUTE ---");
        System.out.print("New Route ID: ");
        String routeId = scanner.nextLine().trim();

        System.out.print("Source Station Name: ");
        Station source = stationService.searchStation(scanner.nextLine().trim());
        if (source == null) {
            System.out.println("[Error] Source station not found.");
            return;
        }

        System.out.print("Destination Station Name: ");
        Station destination = stationService.searchStation(scanner.nextLine().trim());
        if (destination == null) {
            System.out.println("[Error] Destination station not found.");
            return;
        }

        double distance = promptForSafeDouble("Distance (km): ");
        routeService.addRoute(new Route(routeId, source, destination, distance));
    }

    // ---------------- passenger menu ----------------

    private static void showPassengerMenu(Passenger passenger) {
        boolean inPassengerMenu = true;
        while (inPassengerMenu) {
            System.out.println("\n=================================");
            System.out.println("   PASSENGER MENU - " + passenger.getName());
            System.out.println("=================================");
            System.out.println("1. View Profile");
            System.out.println("2. Top Up Balance");
            System.out.println("3. Buy Ticket");
            System.out.println("4. Cancel Ticket");
            System.out.println("5. View My Tickets");
            System.out.println("6. Logout");
            System.out.print("Choose Passenger Operation: ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    passenger.viewProfile();
                    System.out.println("Balance : RM " + String.format("%.2f", passenger.getBalance()));
                    break;
                case "2": passengerTopUp(passenger); break;
                case "3": passengerBuyTicket(passenger); break;
                case "4": passengerCancelTicket(); break;
                case "5": ticketService.viewPassengerTickets(passenger.getUserId()); break;
                case "6":
                    System.out.println("Logging out.");
                    inPassengerMenu = false;
                    break;
                default:
                    System.out.println("Invalid action selection. Choose an option from 1 to 6.");
            }
        }
    }

    private static void passengerTopUp(Passenger passenger) {
        double amount = promptForSafeDouble("Enter top-up amount (RM): ");
        if (amount <= 0) {
            System.out.println("[Error] Top-up amount must be positive.");
            return;
        }

        System.out.println("Select Payment Method: 1. Cash  2. Card");
        Payment payment;
        switch (scanner.nextLine().trim()) {
            case "1":
                payment = new CashPayment();
                break;
            case "2":
                System.out.print("Enter Card Number: ");
                payment = new CardPayment(scanner.nextLine().trim());
                break;
            default:
                System.out.println("[Error] Invalid payment method selection.");
                return;
        }

        if (paymentService.processPayment(payment, amount)) {
            passenger.topUp(amount);
        } else {
            System.out.println("Top-up cancelled: payment was not completed.");
        }
    }

    private static void passengerBuyTicket(Passenger passenger) {
        System.out.println("\n--- BUY TICKET ---");
        System.out.print("Source Station Name: ");
        Station source = stationService.searchStation(scanner.nextLine().trim());
        if (source == null) {
            System.out.println("[Error] Source station not found.");
            return;
        }

        System.out.print("Destination Station Name: ");
        Station destination = stationService.searchStation(scanner.nextLine().trim());
        if (destination == null) {
            System.out.println("[Error] Destination station not found.");
            return;
        }

        Route route = routeService.findRoute(source, destination);
        if (route == null) {
            System.out.println("[Error] No route exists between these two stations.");
            return;
        }

        System.out.println("Select Ticket Type: 1. Single  2. Daily  3. Monthly");
        TicketType ticketType;
        switch (scanner.nextLine().trim()) {
            case "1": ticketType = TicketType.SINGLE; break;
            case "2": ticketType = TicketType.DAILY; break;
            case "3": ticketType = TicketType.MONTHLY; break;
            default:
                System.out.println("[Error] Invalid ticket type selection.");
                return;
        }

        ticketService.buyTicket(passenger, route, ticketType);
    }

    private static void passengerCancelTicket() {
        System.out.print("\nEnter Ticket ID to Cancel: ");
        String ticketId = scanner.nextLine().trim();
        try {
            ticketService.cancelTicket(ticketId);
        } catch (TicketNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    // ---------------- input helpers ----------------

    private static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        int atIndex = email.indexOf('@');
        int lastAtIndex = email.lastIndexOf('@');

        if (atIndex <= 0 || atIndex != lastAtIndex || atIndex == email.length() - 1) {
            return false;
        }

        int dotIndex = email.indexOf('.', atIndex);
        if (dotIndex <= atIndex + 1 || dotIndex == email.length() - 1) {
            return false;
        }

        return true;
    }

    private static String promptForValidEmail(String promptMessage) {
        while (true) {
            System.out.print(promptMessage);
            String email = scanner.nextLine().trim().toLowerCase();

            if (isValidEmail(email)) {
                return email;
            }

            System.out.println("[Error] Please enter a valid email address.");
        }
    }

    private static int promptForSafeInteger(String promptMessage) {
        while (true) {
            System.out.print(promptMessage);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number! Please enter digits only.");
            }
        }
    }

    private static double promptForSafeDouble(String promptMessage) {
        while (true) {
            System.out.print(promptMessage);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number! Please enter a valid amount.");
            }
        }
    }
}