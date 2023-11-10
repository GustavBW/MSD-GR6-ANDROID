package sdu.msd.dtos;

import java.util.Objects;

public final class CreateUserDTO {
    private final String username;
    private final String passwordHash;
    private final String name;
    private final String phoneNumber;
    private final String email;

    CreateUserDTO(String username, String passwordHash, String name, String phoneNumber, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String name() {
        return name;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String email() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        CreateUserDTO that = (CreateUserDTO) obj;
        return Objects.equals(this.username, that.username) &&
                Objects.equals(this.passwordHash, that.passwordHash) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.phoneNumber, that.phoneNumber) &&
                Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwordHash, name, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "CreateUserDTO[" +
                "username=" + username + ", " +
                "passwordHash=" + passwordHash + ", " +
                "name=" + name + ", " +
                "phoneNumber=" + phoneNumber + ", " +
                "email=" + email + ']';
    }

}
