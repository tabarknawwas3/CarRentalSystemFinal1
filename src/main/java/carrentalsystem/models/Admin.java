package carrentalsystem.models;

public class Admin {
    private int adminId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    public Admin() {}

    public int getAdminId()       { return adminId; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public String getEmail()      { return email; }
    public String getPhoneNumber(){ return phoneNumber; }

    public void setAdminId(int id)         { this.adminId = id; }
    public void setFirstName(String fn)    { this.firstName = fn; }
    public void setLastName(String ln)     { this.lastName = ln; }
    public void setUsername(String un)     { this.username = un; }
    public void setPassword(String pw)     { this.password = pw; }
    public void setEmail(String em)        { this.email = em; }
    public void setPhoneNumber(String ph)  { this.phoneNumber = ph; }

    public String getFullName() { return firstName + " " + lastName; }
}