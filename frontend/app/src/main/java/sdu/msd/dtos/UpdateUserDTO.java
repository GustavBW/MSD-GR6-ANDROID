package sdu.msd.dtos;

import java.util.Objects;

public final class UpdateUserDTO {
    private final String username;
    private final String name;
    private final String phoneNumber;
    private final String email;

    UpdateUserDTO(String username, String name, String phoneNumber, String email) {
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String username() {
        return username;
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
        UpdateUserDTO that = (UpdateUserDTO) obj;
        return Objects.equals(this.username, that.username) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.phoneNumber, that.phoneNumber) &&
                Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "UpdateUserDTO[" +
                "username=" + username + ", " +
                "name=" + name + ", " +
                "phoneNumber=" + phoneNumber + ", " +
                "email=" + email + ']';
    }

}
