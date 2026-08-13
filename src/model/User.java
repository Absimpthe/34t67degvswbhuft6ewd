package model;
import enums.UserRole;
public abstract class User {

    //Encapsulaton封装
    private String userId;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    
    public User(String userId , String name , String email, String password ,UserRole role ){
        this. userId = userId;
        this. name = name;
        this. email = email;
        this. password =password;
        this. role = role;

    }

    //Getter
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

    //setter
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



    public void viewProfile() {

        System.out.println("User ID : " + userId);
        System.out.println("Name    : " + name);
        System.out.println("Email   : " + email);
        System.out.println("Role    : " + role);
    }

    public void editProfile(String name, String email) {
    this.name = name;
    this.email = email;
}
    
    public boolean login(String email, String password) {
    return this.email.equals(email) && this.password.equals(password);
    }
}
