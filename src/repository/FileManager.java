package repository;

import exception.FileProcessingException;

/**
 * Interface defining the blueprints for file storage operations.
 * Allows saving and loading of application data collections.
 */
public interface FileManager {
    
    /**
     * Saves a collection data structure (like an ArrayList) into a text file.
     * 
     * @param data     The data collection object to be written out.
     * @param fileName The targeted destination text file path.
     * @throws FileProcessingException If an error occurs during file writing.
     */
    void saveData(Object data, String fileName) throws FileProcessingException;
    
    /**
     * Loads raw text records from a file and maps them into a specific object collection.
     * 
     * @param fileName The source file path to read from.
     * @return A generic Object containing the parsed entity collections.
     * @throws FileProcessingException If the file cannot be read or parsing fails.
     */
    Object loadData(String fileName) throws FileProcessingException;
}