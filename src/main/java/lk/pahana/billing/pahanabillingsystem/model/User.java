package lk.pahana.billing.pahanabillingsystem.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;

    // Constructors
    public User() {
    }

    // Constructor with all properties (including role)
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Constructor without ID (for new users - used in registration)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // ****************************************

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" + // Password එක print කරන්නේ නැහැ ආරක්ෂාවට
                ", role='" + role + '\'' + // Role එකත් print කරන්න
                '}';
    }
}