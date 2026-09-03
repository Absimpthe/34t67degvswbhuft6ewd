package repository;

import model.Admin;
import exception.FileProcessingException;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import enums.UserRole;
import enums.TicketStatus;
import enums.TicketType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TXTFileManager implements FileManager {

    @Override
    public void saveData(Object data, String fileName) throws FileProcessingException {
        if (!(data instanceof ArrayList)) {
            throw new FileProcessingException("Data serialization failed: Expected an ArrayList type.");
        }
        
        ArrayList<?> list = (ArrayList<?>) data;
        
        // Use try to ensure data streams close automatically
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Object obj : list) {
                if (obj == null) continue;
                String line = (obj instanceof User) ? formatUser((User) obj) : obj.toString();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileProcessingException("System failure: Unable to write data to target path '" + fileName + "'");
        }
    }

    @Override
    public Object loadData(String fileName) throws FileProcessingException {
        File file = new File(fileName);
        
        // If no file exists yet, return an empty collection
        if (!file.exists()) {
            if (fileName.toLowerCase().contains("trains")) {
                return new ArrayList<Train>();
            }
            return new ArrayList<Station>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            // Branch logic based on the target filename string pattern
            if (fileName.toLowerCase().contains("stations")) {
                ArrayList<Station> stations = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length == 3) {
                        stations.add(new Station(tokens[0].trim(), tokens[1].trim(), tokens[2].trim()));
                    }
                }
                return stations;
            } else if (fileName.toLowerCase().contains("trains")) {
                ArrayList<Train> trains = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length == 3) {
                        int capacity = Integer.parseInt(tokens[2].trim());
                        trains.add(new Train(tokens[0].trim(), tokens[1].trim(), capacity));
                    }
                }
                return trains;
            }
            
        } catch (IOException e) {
            throw new FileProcessingException("File system error: Failed to process read sequence on '" + fileName + "'");
        } catch (NumberFormatException e) {
            throw new FileProcessingException("Data corruption error: Parsing numeric values out of '" + fileName + "' failed.");
        }
        
        // Return fallback if file type doesn't match other cases
        return new ArrayList<>();
    }

    // typed loaders
    // users.txt row format: userId,name,email,password,role,balance
    // balance for passenger row, admin row just write 0
    public ArrayList<User> loadUsers(String fileName) throws FileProcessingException {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] t = line.split(",");
                if (t.length != 6) continue;

                String userId = t[0].trim();
                String name = t[1].trim();
                String email = t[2].trim();
                String password = t[3].trim();
                UserRole role = UserRole.valueOf(t[4].trim());
                double balance = Double.parseDouble(t[5].trim());

                if (role == UserRole.ADMIN) {
                    users.add(new Admin(userId, name, email, password));
                } else {
                    users.add(new Passenger(userId, name, email, password, balance));
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException("File system error: Failed to process read sequence on '" + fileName + "'", e);
        } catch (IllegalArgumentException e) {
            throw new FileProcessingException("Data corruption error: Invalid user record inside '" + fileName + "'", e);
        }
        return users;
    }

    public ArrayList<Route> loadRoutes(String fileName, ArrayList<Station> stations, ArrayList<Train> trains) throws FileProcessingException {
        ArrayList<Route> routes = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return routes;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] t = line.split(",");
                if (t.length < 5) continue;

                Station source = findStationById(stations, t[1].trim());
                Station destination = findStationById(stations, t[2].trim());
                if (source == null || destination == null) continue;

                double distance = Double.parseDouble(t[3].trim());
                String trainId = t[4].trim();
                Train train = trainId.isEmpty() ? null : findTrainById(trains, trainId);

                routes.add(new Route(t[0].trim(), source, destination, distance, train));
            }
        } catch (IOException e) {
            throw new FileProcessingException("File system error: Failed to process read sequence on '" + fileName + "'", e);
        } catch (NumberFormatException e) {
            throw new FileProcessingException("Data corruption error: Invalid route record inside '" + fileName + "'", e);
        }

        return routes;
    }

    // tickets.txt row format (written by Ticket.toString()): ticketId,passengerUserId,routeId,ticketType,status,fareAmount
    public ArrayList<Ticket> loadTickets(String fileName, HashMap<String, User> users, ArrayList<Route> routes) throws FileProcessingException {
        ArrayList<Ticket> tickets = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return tickets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] t = line.split(",");
                if (t.length != 6) continue;

                User owner = users.get(t[1].trim());
                Route route = findRouteById(routes, t[2].trim());
                if (!(owner instanceof Passenger) || route == null) continue; // skip an orphaned ticket

                TicketType type = TicketType.valueOf(t[3].trim());
                TicketStatus status = TicketStatus.valueOf(t[4].trim());
                double fare = Double.parseDouble(t[5].trim());

                tickets.add(new Ticket(t[0].trim(), (Passenger) owner, route, type, status, fare));
            }
        } catch (IOException e) {
            throw new FileProcessingException("File system error: Failed to process read sequence on '" + fileName + "'", e);
        } catch (IllegalArgumentException e) {
            throw new FileProcessingException("Data corruption error: Invalid ticket record inside '" + fileName + "'", e);
        }
        return tickets;
    }

    // private helpers

    private String formatUser(User user) {
        double balance = (user instanceof Passenger) ? ((Passenger) user).getBalance() : 0.0;
        return user.getUserId() + "," + user.getName() + "," + user.getEmail() + ","
                + user.getPassword() + "," + user.getRole() + "," + balance;
    }

    private Station findStationById(ArrayList<Station> stations, String id) {
        for (Station s : stations) {
            if (s.getStationId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }

    private Train findTrainById(ArrayList<Train> trains, String id) {
        for (Train t : trains) {
            if (t.getTrainId().equalsIgnoreCase(id)) return t;
        }
        return null;
    }
    
    private Route findRouteById(ArrayList<Route> routes, String id) {
    	for (Route r : routes) {
    		if (r.getRouteId().equalsIgnoreCase(id)) return r;
    	}
    	return null;
    }

}