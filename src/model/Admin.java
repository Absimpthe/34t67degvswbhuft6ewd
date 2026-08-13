package model;

import enums.UserRole;

public class Admin extends User {

    public Admin(String userId,String name,String email,String password) {

        super(userId, name, email, password, UserRole.ADMIN);
    }

    public void manageSystem() {
        System.out.println("Admin is managing the system.");
    }

}
