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
import payment.EWalletPayment;
import payment.EWalletProvider;
import payment.Payment;
import enums.TicketType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
    private static final int MIN_PASSWORD_LENGTH = 6;

    private static UserService userService = new UserService();
    private static StationService stationService = new StationService();
    private static TrainService trainService = new TrainService();
    private static RouteService routeService = new RouteService();
    private static TicketService ticketService = new TicketService();
    private static PaymentService paymentService = new PaymentService();
    private static ReportService reportService = new ReportService();
    private static TXTFileManager fileManager = new TXTFileManager();

    private static Scanner scanner = new Scanner(System.in);
    private static Set<String> protectedFiles = new HashSet<>(); //a corrupt file never wipes out content in it

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
            stationService.setStations(castStations(fileManager.loadData(STATIONS_FILE)));
        }catch(FileProcessingException e){
            markLoadFailure(STATIONS_FILE, e);
        }

        try {
            trainService.setTrains(castTrains(fileManager.loadData(TRAINS_FILE)));
        } catch (FileProcessingException e) {
            markLoadFailure(TRAINS_FILE, e);
        }

        try {
            for (User u : fileManager.loadUsers(USERS_FILE)) {
                userService.registerUser(u);
            }
        } catch (FileProcessingException e) {
            markLoadFailure(USERS_FILE, e);
        }

        // routes point to stations and trains; if either failed, routes cannot be rebuilt correctly
        if (protectedFiles.contains(STATIONS_FILE) || protectedFiles.contains(TRAINS_FILE)) {
            protectedFiles.add(ROUTES_FILE);
        } else {
            try {
                routeService.setRoutes(
                    fileManager.loadRoutes(ROUTES_FILE, stationService.getStations(), trainService.getTrains()));
            } catch (FileProcessingException e) {
                markLoadFailure(ROUTES_FILE, e);
            }
        }

        // tickets point to users and routes; if either failed, tickets cannot be rebuilt correctly
        if (protectedFiles.contains(USERS_FILE) || protectedFiles.contains(ROUTES_FILE)) {
            protectedFiles.add(TICKETS_FILE);
        } else {
            try {
                ticketService.setTickets(
                    fileManager.loadTickets(TICKETS_FILE, userService.getUsers(), routeService.getRoutes()));
            } catch (FileProcessingException e) {
                markLoadFailure(TICKETS_FILE, e);
            }
        }

        if (protectedFiles.isEmpty()) {
            System.out.println("[System Initialization] Local database files mapped successfully.");
        } else {
            System.out.println("[Warning] These files were not fully loaded and will NOT be overwritten on exit: " + protectedFiles);
            System.out.println("[Warning] Fix or remove the affected file(s), then restart the system.");
        }

        if (userService.findUser(DEFAULT_ADMIN_ID) == null) {
            userService.registerUser(new Admin(DEFAULT_ADMIN_ID, "System Admin", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD));
        }
    }

    private static void markLoadFailure(String fileName, FileProcessingException exp) {
        System.out.println("[Warning] File restore bypassed: " + exp.getMessage());
        protectedFiles.add(fileName);
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
        boolean allSaved = true;
        allSaved &= saveFile(new ArrayList<User>(userService.getUsers().values()), USERS_FILE);
        allSaved &= saveFile(stationService.getStations(), STATIONS_FILE);
        allSaved &= saveFile(trainService.getTrains(), TRAINS_FILE);
        allSaved &= saveFile(routeService.getRoutes(), ROUTES_FILE);
        allSaved &= saveFile(ticketService.getTickets(), TICKETS_FILE);

        if (allSaved) {
            System.out.println("[System Shutdown] Data successfully synchronized. Safe travels!");
        } else {
            System.out.println("[System Shutdown] Some files were not saved. See the messages above.");
        }
    }

    // saves one file, skipping it if it failed to load at startup (so its records are not wiped)
    private static boolean saveFile(ArrayList<?> data, String fileName) {
        if (protectedFiles.contains(fileName)) {
            System.out.println("[Warning] " + fileName + " was left untouched because it failed to load at startup.");
            return false;
        }
        try {
            fileManager.saveData(data, fileName);
            return true;
        } catch (FileProcessingException e) {
            System.out.println("[Critical Failure] Unable to save " + fileName + ": " + e.getMessage());
            return false;
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
        
        String name;
        while(true){ //name
            System.out.print("Enter Full Name: ");
            name = scanner.nextLine().trim();
            if(name.isEmpty()){
                System.out.println("[Error] Registration failed: Name cannot be empty.");
            }else if(containsComma(name)){
                System.out.println("[Error] Registration failed: Name cannot contain commas.");
            }else{
                break;
            }
        }

        String email;
        while(true){ //email
            email = promptForValidEmail("Enter Email: ");
            
            if(email.isEmpty()) {
                System.out.println("[Error] Registration failed: Email cannot be empty.");
                return;
            }
            if(userService.findUserByEmail(email) != null){
                System.out.println("[Error] Registration failed: Email is already registered. Please use a different email.");
            } else {
                break;
            }
        }

        String password;
        while(true){ //password
            System.out.print("Enter Password: ");
            password = scanner.nextLine().trim();
            if(password.isEmpty()){
                System.out.println("[Error] Registration failed: Password cannot be empty.");
            }else if(containsComma(password)){
                System.out.println("[Error] Registration failed: Password cannot contain commas.");
            }else if(password.length() < MIN_PASSWORD_LENGTH){
                System.out.println("[Error] Registration failed: Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
            }else{
                break;
            }
        }

        if (userService.findUserByEmail(email) != null) {
            System.out.println("[Error] Registration failed: Email is already registered.");
            return;
        }

        String userId = userService.generatePassengerId();
        userService.registerUser(new Passenger(userId, name, email, password, 0.0));
        System.out.println("Registration successful! Your User ID is " + userId + ". You may now log in as " + name + ".");
    }

    // ---------------- admin menu ----------------

    private static void showAdminMenu(Admin admin) {
        boolean inAdminMenu = true;
        while (inAdminMenu) {
            System.out.println("\n=====================================");
            System.out.println("   ADMIN DASHBOARD - " + admin.getName());
            System.out.println("=====================================");
            System.out.println("1 to 4 : Station Management");
            System.out.println("1. Add Station");
            System.out.println("2. View Stations");
            System.out.println("3. Search Station");
            System.out.println("4. Sort Stations by Name");
            System.out.println("-------------------------------------");
            System.out.println("   5 to 8 : Train & Route Management");
            System.out.println("5. Add Train");
            System.out.println("6. View Trains");
            System.out.println("7. Create Route");
            System.out.println("8. View Routes");
            System.out.println("-------------------------------------");
            System.out.println("   9 to 13 : Ticketing & Reporting");
            System.out.println("9. View All Tickets");
            System.out.println("10. Sort Tickets by Status");
            System.out.println("11. Generate Report");
            System.out.println("12. View All Users");
            System.out.println("13. Logout");
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
                case "9": ticketService.viewTickets(); break;
                case "10": adminSortTicketsByStatus(); break;
                case "11": reportService.generateReport(ticketService.getTickets()); 
                           reportService.showPaymentSummary(paymentService);   
                           break;
                case "12":
                    userService.viewAllUsers(); break;
                case "13":
                    System.out.println("Logging out.");
                    inAdminMenu = false;
                    break;
                default:
                    System.out.println("Invalid action selection. Choose an option from 1 to 13.");
            }
        }
    }
    
    private static void adminSortTicketsByStatus() {
        ArrayList<Ticket> tickets = ticketService.getTickets();

        if (tickets.isEmpty()) {
            System.out.println("No tickets available to sort.");
            return;
        }

        Collections.sort(tickets, new Comparator<Ticket>() {
            @Override
            public int compare(Ticket t1, Ticket t2) {
                return Integer.compare(statusRank(t1), statusRank(t2));
            }

            private int statusRank(Ticket ticket) {
                if (ticket.getStatus() == enums.TicketStatus.ACTIVE) {
                    return 1;
                }
                if (ticket.getStatus() == enums.TicketStatus.USED) {
                    return 2;
                }
                if (ticket.getStatus() == enums.TicketStatus.CANCELLED) {
                    return 3;
                }
                return 4;
            }
        });

        System.out.println("Tickets sorted by status: ACTIVE, USED, CANCELLED");
        ticketService.viewTickets();
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

        if(containsComma(id, name, location)){
            System.out.println("[Error] Station ID, Name, and Location cannot contain commas.");
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
    
        String id;
        while(true){
            System.out.print("Enter Train ID: ");
            id = scanner.nextLine().trim();
        
            if(id.isEmpty()){
                System.out.println("[Error] Train ID cannot be empty.");
            }else if(trainService.trainIdExists(id)){
                System.out.println("[Error] Train ID " + id + " already exists. IDs must be unique.");
            }else if(id.contains(",")) {
                System.out.println("[Error] Train ID and Name    cannot contain commas.");
            }else {
                break;
            } 
        }

        String name;
        while(true){
            System.out.print("Enter Train Name: ");
            name = scanner.nextLine().trim();
            
            if(name.isEmpty()){
                System.out.println("[Error] Train Name cannot be empty.");
            }else if(name.contains(",")){
                System.out.println("[Error] Train Name cannot contain commas.");
            }else{
                break;
            }
        }

        int capacity = 0;
        while (true) {
            capacity = promptForSafeInteger("Capacity: ");
        
            if (capacity == 0) {
                System.out.println("[Error] Capacity cannot be empty or zero. Please try again.");
            } else if (capacity <= 0) { 
                System.out.println("[Error] Train capacity must be greater than 0.");
            }  else {
                break; // Capacity is valid, break out of the loop
            }
        } 
        
        trainService.addTrain(new Train(id, name, capacity));
    }

    private static void adminCreateRoute() {
        System.out.println("\n--- CREATE ROUTE ---");
        
        if (stationService.getStations().size() < 2) {
            System.out.println("[Error] At least two stations are needed to create a route. Add stations first.");
            return;
        }
        if (trainService.getTrains().isEmpty()) {
            System.out.println("[Error] No trains available. Add a train first.");
            return;
        }

        String routeId;
        while(true){
            System.out.print("Route ID: ");
            routeId = scanner.nextLine().trim();
            if(routeId.isEmpty()){
                System.out.println("[Error] Route ID cannot be empty.");
            }else if(routeService.routeIdExists(routeId)){
                System.out.println("[Error] Route ID " + routeId + " already exists. IDs must be unique.");
            }else if(routeId.contains(",")){
                System.out.println("[Error] Route ID cannot contain commas.");
            }else{
                break;
            }
        }

        Station source = null;
        Station destination = null;

        while(true){
            while(true){
                System.out.print("Source Station Name: ");
                source = stationService.searchStation(scanner.nextLine().trim());   

                if(source == null){
                    System.out.println("[Error] Source station not found. Please enter a valid station name."); 
                }else{
                    break;
                }
            }
            while(true){
                
                System.out.print("Destination Station Name: ");
                destination = stationService.searchStation(scanner.nextLine().trim());

                if(destination == null){
                    System.out.println("[Error] Destination station not found. Please enter a valid station name.");
                }else{
                    break;
                }
            }

            if(source.getStationId().equalsIgnoreCase(destination.getStationId())){
                System.out.println("[Error] Source and destination stations cannot be the same. Please enter different stations.");
                System.out.println("        Please re-enter the source and destination stations.");
            }else{
                break;
            }
        }
        
        double distance;
        while(true){
            distance = promptForSafeDouble("Distance (in km): ");
            if(distance <= 0){
                System.out.println("[Error] Distance must be greater than 0. Please enter a valid distance.");
            }else{
                break;
            }
        }

        if (trainService.getTrains().isEmpty()) {
            System.out.println("[Error] No trains available. Add a train first.");
            return;
        }

        System.out.println("\nAvailable Trains:");
        trainService.viewTrains();

        Train assignedTrain = null;
        while(assignedTrain == null){
            System.out.print("Enter Train ID to assign to this route: ");
            String trainId = scanner.nextLine().trim();

            if(trainId.isEmpty()){
                System.out.println("[Error] Train ID cannot be empty. Please enter a valid Train ID.");
                continue;
            }

            for(Train train : trainService.getTrains()){
                if(train.getTrainId().equalsIgnoreCase(trainId)){
                    assignedTrain = train;
                    break;
                }  
            }

            if(assignedTrain == null){
                System.out.println("[Error] Train ID " + trainId + " not found. Please enter a valid one.");
            }
        }
        
        routeService.addRoute(new Route(routeId, source, destination, distance, assignedTrain));
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
            System.out.println("4. Use Ticket");
            System.out.println("5. Cancel Ticket");
            System.out.println("6. View My Tickets");
            System.out.println("7. Logout");
            System.out.print("Choose an action: ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    passenger.viewProfile();
                    System.out.printf("Balance : RM " + String.format("%.2f", passenger.getBalance()));
                    break;
                case "2": 
                	passengerTopUp(passenger); 
                	break;
                case "3": 
                	passengerBuyTicket(passenger); 
                	break;
                case "4":
                    passengerUseTicket(passenger);
                    break;
                case "5":
                    passengerCancelTicket(passenger);
                    break;
                case "6":
                    ticketService.viewPassengerTickets(passenger.getUserId());
                    break;
                case "7":
                    System.out.println("Logging out.");
                    inPassengerMenu = false;
                    break;
                default:
                    System.out.println("Invalid action selection. Choose an option from 1 to 7.");
            }
        }
    }

    private static void passengerTopUp(Passenger passenger) {
        double amount = promptForSafeDouble("Enter top-up amount (RM): ");
        if (amount <= 0) {
            System.out.println("[Error] Top-up amount must be positive.");
            return;
        }

        System.out.println("Select Payment Method: 1. Cash  2. Card 3. E-Wallet");
        Payment payment;
        switch (scanner.nextLine().trim()) {
            case "1":
                payment = new CashPayment();
                break;
            case "2":
                String cardNumber;
                while(true){
                    System.out.print("Enter card number (14-19 digits, 0 to cancel");
                    cardNumber = scanner.nextLine().trim().replace(" ", "");
                    if(cardNumber.equals("0")){
                        System.out.println("Top-up cancelled.");
                        return;
                    }

                    if(CardPayment.isValidCardNumber(cardNumber)){
                        break;
                    }
                    System.out.println("[Error] Invalid card number. It must be 14 to 19 digits with no letters. Please try again.");
                }
                payment = new CardPayment(cardNumber);
                break;
            case "3":
                EWalletProvider provider = promptForEWalletProvider();
                if(provider == null){
                    System.out.println("[Error] Invalid E-Wallet provider selection.");
                    return;
                }
                
                String walletEmail;
                while(true){
                    System.out.print("Enter the email linked to your " + provider.getLabel() + " account (0 to cancel): ");
                    walletEmail = scanner.nextLine().trim().toLowerCase();
                    if(walletEmail.equals("0")){
                        System.out.println("Top-up cancelled.");
                        return;
                    }

                    if(isValidEmail(walletEmail)){
                        break;
                    }
                    System.out.println("[Error] Please enther a valid email address (e.g alice@mail.com).");  
                }
                payment = new EWalletPayment(provider, walletEmail);
                break;
            default:
                System.out.println("[Error] Invalid payment method selection.");
                return;
        }
        System.out.printf("Confirm top-up of RM %.2f via %s? (Y/N): ", amount, payment.getMethodName());
        if(!scanner.nextLine().trim().equalsIgnoreCase("Y")){
            System.out.println("Payment cancelled. Top-up not applied.");
            return;
        }
        if (paymentService.processPayment(payment, amount)) {
            passenger.topUp(amount);
        } else {
            System.out.println("Top-up cancelled: payment was not completed.");
        }
    }
    // list every provider in the enum, then return one
    private static EWalletProvider promptForEWalletProvider() {
        EWalletProvider[] providers = EWalletProvider.values();
        System.out.println("Select E-Wallet Provider:");
        for (int i = 0; i < providers.length; i++) {
            System.out.println((i + 1) + ". " + providers[i].getLabel());
        }
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= providers.length) {
                return providers[choice - 1];
            }
        } catch (NumberFormatException e) {
        }
        return null;
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
        
        System.out.println("Assigned Train: " + (route.getTrain() != null
                ? route.getTrain().getTrainName() + " (" + route.getTrain().getTrainId() + ")"
                : "None"));

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

    private static void passengerCancelTicket(Passenger passenger) {
        System.out.print("\nEnter Ticket ID to Cancel: ");
        String ticketId = scanner.nextLine().trim();
        try {
            ticketService.cancelTicket(ticketId, passenger.getUserId());
        } catch (TicketNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }
    
    private static void passengerUseTicket(Passenger passenger) {
        System.out.print("\nEnter Ticket ID to Mark as Used: ");
        String ticketId = scanner.nextLine().trim();

        try {
            ticketService.useTicket(ticketId, passenger.getUserId());
        } catch (TicketNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    // ---------------- input helpers ----------------

    private static boolean isValidEmail(String email) {
        if (email == null || email.isBlank() || email.contains(",")) {
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

    private static boolean containsComma(String... fields) {
        for (String field : fields) {
            if (field != null && field.contains(",")) {
                return true;
            }
        }
        return false;
    }
}