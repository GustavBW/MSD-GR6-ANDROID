package gbw.sdu.msd.backend.dtos;

public record InvoiceDTO(int id, UserDTO paymentFrom, UserDTO paymentTo, double amount) {
}
