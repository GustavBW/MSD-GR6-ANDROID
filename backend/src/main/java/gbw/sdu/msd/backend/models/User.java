package gbw.sdu.msd.backend.models;


public class User {

    private final int id;
    private String username, password, email, phoneNumber, name;

    public User(int id, String username, String password, String email, String phoneNumber, String name){
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
    public String name() {return name;}
    public void setName(String name){this.name = name;}

    public int id() {
        return id;
    }

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String email() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString(){
        return "User{"+username+"|"+name+"|pass="+password+"}";
    }
}
