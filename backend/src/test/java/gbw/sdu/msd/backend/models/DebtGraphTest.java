package gbw.sdu.msd.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DebtGraphTest {
    private DebtGraph debtGraph;
    private User entityA;
    private User entityB;
    private User entityC;

    @BeforeEach
    public void setUp() {
        debtGraph = new DebtGraph();
        entityA = new User(0, "username", "password", "email", "phoneNumber", "name");
        entityB = new User(1, "username", "password", "email", "phoneNumber", "name");
        entityC = new User(2, "username", "password", "email", "phoneNumber", "name");
    }

    @Test
    public void testAddDebt() {
        debtGraph.addDebt(entityA, entityB, 100.0);
        debtGraph.addDebt(entityB, entityA, 50.0);

        Map<User, Double> debtsFromA = debtGraph.whoDoesThisUserOweMoney(entityA);
        Map<User, Double> debtsFromB = debtGraph.whoDoesThisUserOweMoney(entityB);

        assertEquals(100.0, debtsFromA.get(entityB));
        assertEquals(50.0, debtsFromB.get(entityA));
    }

    @Test
    public void testTotalOwedByUser() {
        debtGraph.addDebt(entityA, entityB, 100.0);
        debtGraph.addDebt(entityA, entityC, 75.0);

        assertEquals(175.0, debtGraph.totalOwedByUser(entityA));
    }

    @Test
    public void testTotalOwedToUser() {
        debtGraph.addDebt(entityA, entityB, 100.0);
        debtGraph.addDebt(entityC, entityA, 50.0);

        assertEquals(50.0, debtGraph.totalOwedToUser(entityA));
    }

    @Test
    public void testGetTotalDebtToSet() {
        debtGraph.addDebt(entityA, entityB, 100.0);
        debtGraph.addDebt(entityA, entityC, 75.0);

        List<User> creditorsToCheck = List.of(entityB, entityC);
        double totalDebtToCreditors = debtGraph.totalDeptToGroup(entityA, creditorsToCheck);

        assertEquals(175.0, totalDebtToCreditors);
    }

    @Test
    public void testWhoOwesMoneyToThisUser() {
        debtGraph.addDebt(entityA, entityB, 100.0);
        debtGraph.addDebt(entityC, entityA, 50.0);

        Map<User, Double> debtsToA = debtGraph.whoOwesMoneyToThisUser(entityA);

        assertEquals(100.0, debtsToA.get(entityB));
        assertEquals(50.0, debtsToA.get(entityC));
    }
}
