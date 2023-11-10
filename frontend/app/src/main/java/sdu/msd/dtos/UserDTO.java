package sdu.msd.dtos;


import java.util.List;
import java.util.Objects;

public final class UserDTO {
    private final int id;
    private final String username;
    private final String name;
    private final String email;
    private final String phoneNumber;

    UserDTO(int id, String username, String name, String email, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        UserDTO that = (UserDTO) obj;
        return this.id == that.id &&
                Objects.equals(this.username, that.username) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "UserDTO[" +
                "id=" + id + ", " +
                "username=" + username + ", " +
                "name=" + name + ", " +
                "email=" + email + ", " +
                "phoneNumber=" + phoneNumber + ']';
    }
}
