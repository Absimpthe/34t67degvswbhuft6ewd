package repository;

import exception.FileProcessingException;

public interface FileManager {
    
    void saveData(Object data, String fileName) throws FileProcessingException;
    
    Object loadData(String fileName) throws FileProcessingException;
}