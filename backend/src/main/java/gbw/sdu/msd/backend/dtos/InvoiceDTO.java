package gbw.sdu.msd.backend.dtos;

public record InvoiceDTO(UserDTO paymentFrom, UserDTO paymentTo, double amount) {
}
