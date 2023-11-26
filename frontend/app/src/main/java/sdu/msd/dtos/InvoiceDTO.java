package sdu.msd.dtos;

public class InvoiceDTO {
    private int id;
    private UserDTO paymentFrom;
    private UserDTO paymentTo;
    private double amount;

    public InvoiceDTO(int id, UserDTO paymentFrom, UserDTO paymentTo, double amount) {
        this.id = id;
        this.paymentFrom = paymentFrom;
        this.paymentTo = paymentTo;
        this.amount = amount;
    }

    public int id() {
        return id;
    }

    public UserDTO paymentFrom() {
        return paymentFrom;
    }

    public UserDTO paymentTo() {
        return paymentTo;
    }

    public double amount() {
        return amount;
    }
}
