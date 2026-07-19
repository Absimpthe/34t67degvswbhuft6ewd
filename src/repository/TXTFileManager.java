package repository;

import model.Station;
import model.Train;
import exception.FileProcessingException;

import java.io.*;
import java.util.ArrayList;

/**
 * Text file implementation of the FileManager interface.
 * Handles reading and writing data records using comma-separated value formatting.
 */
public class TXTFileManager implements FileManager {

    @Override
    public void saveData(Object data, String fileName) throws FileProcessingException {
        // Enforce that incoming data structures must be valid Lists
        if (!(data instanceof ArrayList)) {
            throw new FileProcessingException("Data serialization failed: Expected an ArrayList type.");
        }
        
        ArrayList<?> list = (ArrayList<?>) data;
        
        // Use try-with-resources to ensure data streams close automatically
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Object obj : list) {
                if (obj != null) {
                    // Leverages the object's overridden toString() method directly
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
        
        // Graceful handling: If no data file exists yet, return an empty initialized collection
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
                    // Expects exactly 3 parameters: stationId, name, location
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
                    // Expects exactly 3 parameters: trainId, trainName, capacity
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
        
        // Return fallback safe instance if file type doesn't explicitly match predefined cases
        return new ArrayList<>();
    }
}