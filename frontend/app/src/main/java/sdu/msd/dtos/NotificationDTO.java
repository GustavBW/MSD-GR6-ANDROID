package sdu.msd.dtos;


import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class NotificationDTO {
    private final String title;
    private final String message;

    NotificationDTO(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String title() {
        return title;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        NotificationDTO that = (NotificationDTO) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, message);
    }

    @Override
    public String toString() {
        return "NotificationDTO[" +
                "title=" + title + ", " +
                "message=" + message + ']';
    }
}
