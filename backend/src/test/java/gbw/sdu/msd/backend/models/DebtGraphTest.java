package gbw.sdu.msd.backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DebtGraphTest {
    private DebtGraph debtGraph;
    private User userA;
    private User userB;
    private User userC;
    private static final double FLOAT_SIGNIFICANCE_LEVEL = 0.01;
    private static final long MAX_MS_PER_INSERT = 1L;
    private static final long MAX_MS_PER_PAYMENT = 3L;

    @BeforeEach
    public void setUp() {
        debtGraph = new DebtGraph();
        userA = new User(0, "username", "password", "email", "phoneNumber", "A");
        userB = new User(1, "username", "password", "email", "phoneNumber", "B");
        userC = new User(2, "username", "password", "email", "phoneNumber", "C");
    }

    @Test
    public void recordDebtCancellation() {
        //if 2 users owe each other
        debtGraph.recordDebt(userA, userB, 100.0);
        debtGraph.recordDebt(userB, userA, 50.0);
        //Since A owes B 100, but B owes A 50 in return, A owes B 50.
        assertEquals(50.0, debtGraph.getAmountOwedBy(userA, userB));
        //B did owe A 50, however, due A owing B 100 in the first place, those cancel out.
        assertEquals(0.0, debtGraph.getAmountOwedBy(userB, userA));

        //Regardless of order.
        debtGraph = new DebtGraph();
        //if 2 users owe each other
        debtGraph.recordDebt(userB, userA, 50.0);
        debtGraph.recordDebt(userA, userB, 100.0);
        //Since A owes B 100, but B owes A 50 in return, A owes B 50.
        assertEquals(50.0, debtGraph.getAmountOwedBy(userA, userB));
        //B did owe A 50, however, due A owing B 100 in the first place, those cancel out.
        assertEquals(0.0, debtGraph.getAmountOwedBy(userB, userA));
    }
    @Test
    public void recordDebtAddition() {
        debtGraph.recordDebt(userA, userB, 100.0);
        debtGraph.recordDebt(userA, userB, 50.0);
        assertEquals(150.0, debtGraph.getAmountOwedBy(userA, userB));
        //should be unordered. So. Reset:
        debtGraph = new DebtGraph();
        debtGraph.recordDebt(userA, userB, 50.0);
        debtGraph.recordDebt(userA, userB, 100.0);
        assertEquals(150.0, debtGraph.getAmountOwedBy(userA, userB));
    }
    @Test
    public void insertionSpeedLessThanAllowed(){
        int[] numUsers = {1,10,100,1000,10_000,100_000,1_000_000,10_000_000};
        for(int amount : numUsers){
            List<User> users = new ArrayList<>(amount);
            for(int i = 0; i < amount; i++){
                users.add(new User(i, i + "", i + "", i + "", i + "", i + ""));
            }
            debtGraph = new DebtGraph();
            long timeA = System.currentTimeMillis();
            for(int i = 0; i < 1_000_000; i++){
                //prevents VM caching and skipping
                debtGraph.recordDebt(users.get(i % amount), users.get((i + 1) % amount), (i % 10) * 100);
            }
            long deltaMs = System.currentTimeMillis() - timeA;
            System.out.println("1_000_000 inserts total time:\t" + deltaMs + "ms,\tavg per insert:\t" + ((double) deltaMs / 1_000_000) + "ms\twith: " + amount + " users");
            assertTrue(deltaMs / 1_000_000 < MAX_MS_PER_INSERT);
        }
    }

    @Test
    public void recordDebtTransference(){
        debtGraph.recordDebt(userA, userB, 50);
        debtGraph.recordDebt(userB, userC, 50);
        //User A should now owe C 50 instead of B - simplifying the graph
        assertEquals(0.0, debtGraph.getAmountOwedBy(userB, userC));
        assertEquals(50, debtGraph.getAmountOwedBy(userA, userC));

        //And it should be "omnidirectional" - regardless of insertion order
        debtGraph = new DebtGraph();
        debtGraph.recordDebt(userB, userC, 50);
        debtGraph.recordDebt(userA, userB, 50);
        //User A should now owe C 50 instead of B - simplifying the graph
        assertEquals(0.0, debtGraph.getAmountOwedBy(userB, userC));
        assertEquals(50, debtGraph.getAmountOwedBy(userA, userC));
    }

    @Test
    public void testProcessPayment() {
        debtGraph.recordDebt(userA, userB, 50.0);
        assertEquals(50.0, debtGraph.getAmountOwedBy(userA, userB));
        assertEquals(0.0, debtGraph.getAmountOwedBy(userB, userA));
        System.out.println(debtGraph);

        debtGraph.recordDebt(userB, userC, 50.0);
        System.out.println(debtGraph);

        //Since A already owed B those 50 buck, A now owes C 50 and B 0
        assertEquals(0.0, debtGraph.getAmountOwedBy(userB, userC));
        assertEquals(50.0, debtGraph.getAmountOwedBy(userA, userC));
    }


    @Test
    public void testTotalOwedByUser() {
        debtGraph.recordDebt(userA, userB, 100.0);
        debtGraph.recordDebt(userA, userC, 75.0);

        assertEquals(175.0, debtGraph.totalOwedByUser(userA));
    }

    @Test
    public void testTotalOwedToUser() {
        debtGraph.recordDebt(userA, userB, 100.0);
        debtGraph.recordDebt(userC, userA, 50.0);

        assertEquals(0.0, debtGraph.totalOwedToUser(userA));
        assertEquals(100.0, debtGraph.totalOwedToUser(userB));
        assertEquals(50.0, debtGraph.getAmountOwedBy(userA, userB));
        assertEquals(50.0, debtGraph.getAmountOwedBy(userC, userB));
    }

    @Test
    public void testGetTotalDebtToSet() {
        debtGraph.recordDebt(userA, userB, 100.0);
        debtGraph.recordDebt(userA, userC, 75.0);

        List<User> creditorsToCheck = List.of(userB, userC);
        double totalDebtToCreditors = debtGraph.totalDeptToGroup(userA, creditorsToCheck);

        assertEquals(175.0, totalDebtToCreditors);
    }

    @Test
    public void testWhoOwesMoneyToThisUser() {
        debtGraph.recordDebt(userA, userB, 100.0);
        debtGraph.recordDebt(userC, userA, 50.0);

        assertEquals(0.0,debtGraph.getAmountOwedBy(userB, userA));
        assertEquals(0.0, debtGraph.getAmountOwedBy(userC, userA));
        assertEquals(50.0, debtGraph.getAmountOwedBy(userC, userB));
        assertEquals(50.0, debtGraph.getAmountOwedBy(userA, userB));
    }
}
