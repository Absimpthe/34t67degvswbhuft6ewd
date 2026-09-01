package service;

import java.util.HashMap;

import exception.InvalidLoginException;
import model.User;

public class UserService {

    private HashMap<String, User> users;

    public UserService() {
        users = new HashMap<>();
    }

    public void register(User user) {
        users.put(user.getUserId(), user);
    }

    public User login(String email, String password)
            throws InvalidLoginException {

        User user = findUserByEmail(email);

        if (user == null) {
            throw new InvalidLoginException("User not found.");
        }

        if (!user.getPassword().equals(password)) {
            throw new InvalidLoginException("Wrong password.");
        }

        return user;
    }

    public User findUser(String userId) {
        return users.get(userId);
    }

    public User findUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    private int getMaxNumericId(String prefix) {
        int max = -1;

        for (User user : users.values()) {
            String id = user.getUserId();
            if (id != null && id.startsWith(prefix)) {
                String numberPart = id.substring(prefix.length());
                try {
                    int value = Integer.parseInt(numberPart);
                    if (value > max) {
                        max = value;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return max;
    }

    public String generatePassengerId() {
        int next = getMaxNumericId("PS") + 1;
        return String.format("PS%03d", next);
    }

    public String generateAdminId() {
        int next = getMaxNumericId("AD") + 1;
        return String.format("AD%03d", next);
    }

    //getter
    public HashMap<String, User> getUsers() {
        return users;
    }

}