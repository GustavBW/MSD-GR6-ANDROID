package gbw.sdu.msd.backend.models;

/**
 *
 * @param accountable userId of who should pay
 * @param collector userId of who should receive the money
 * @param amount the amount of money
 */
public record Debt(int accountable, int collector, float amount) {
}
