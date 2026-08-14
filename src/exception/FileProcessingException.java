package exception;

// thrown whenever TXTFileManager fails to read, write, or parse data files
public class FileProcessingException extends Exception {
    public FileProcessingException(String message) {
        super(message);
    }

    // preserves the original error (e.g. IOException) so the root cause
    // isn't lost when we wrap it into our own exception type
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
