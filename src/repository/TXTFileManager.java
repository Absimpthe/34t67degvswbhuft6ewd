package repository;

import model.Station;
import model.Train;
import exception.FileProcessingException;

import java.io.*;
import java.util.ArrayList;

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
                if (obj != null) {
                    // Uses the overridden toString() method
                    writer.write(obj.toString());
                    writer.newLine();
                }
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
                    // Needs 3 params: stationId, name, location
                    if (tokens.length == 3) {
                        stations.add(new Station(tokens[0].trim(), tokens[1].trim(), tokens[2].trim()));
                    }
                }
                return stations;
            } 
            else if (fileName.toLowerCase().contains("trains")) {
                ArrayList<Train> trains = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    // Needs 3 params: trainId, trainName, capacity
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
}