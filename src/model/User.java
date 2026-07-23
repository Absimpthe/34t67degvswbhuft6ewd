package model;
import enums.UserRole;
public class User {

    private String userId;
    private String name;
    private String email;
    private String password;
    private UserRole role;

    public User(){

    }
    
    public User(String userID , String name , String email, String password ,UserRole role ){
        this. userId = userID;
        this. name = name;
        this. email = email;
        this. password =password;
        this. role = role;

    }

    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public UserRole getRole(){
        return role;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }



    public void displayProfile() {

        System.out.println("User ID : " + userId);
        System.out.println("Name    : " + name);
        System.out.println("Email   : " + email);
        System.out.println("Role    : " + role);
    }
}
