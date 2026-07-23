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

    public User login(String userId, String password)
            throws InvalidLoginException {

        User user = users.get(userId);

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

    public HashMap<String, User> getUsers() {

        return users;

    }

}