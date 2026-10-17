package com.jain.ladder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EloTest {

    @Test
    @DisplayName("Expected probability symmetry: expected(a,b) + expected(b,a) must equal 1.0")
    void expectedSymmetry() {
        double probA = Elo.expected(1200, 1400);
        double probB = Elo.expected(1400, 1200);
        assertEquals(1.0, probA + probB, 1e-9, "Probabilities must sum to 1.0");
    }

    @Test
    @DisplayName("Equal rating gives 50% probability")
    void equalRatingFiftyPercent() {
        assertEquals(0.5, Elo.expected(1500, 1500), 1e-9);
    }

    @Test
    @DisplayName("K-Factor follows provisional vs established thresholds")
    void kFactorThresholds() {
        // Less than 30 games -> provisional K=40
        assertEquals(40, Elo.kFactor(1200, 10));
        assertEquals(40, Elo.kFactor(2500, 29));

        // Established players (<2400) -> K=20
        assertEquals(20, Elo.kFactor(1800, 30));
        assertEquals(20, Elo.kFactor(2399, 50));

        // Master players (>=2400) -> K=10
        assertEquals(10, Elo.kFactor(2400, 35));
        assertEquals(10, Elo.kFactor(2600, 100));
    }

    @Test
    @DisplayName("Rating conservation: points gained must equal points lost when K-factor matches")
    void ratingConservation() {
        Elo.Player playerA = new Elo.Player(1500, 50);
        Elo.Player playerB = new Elo.Player(1500, 50);

        // Player A wins
        Elo.Result result = Elo.play(playerA, playerB, 1.0);
        assertEquals(result.deltaA(), -result.deltaB(), "Points gained must equal points lost");
        assertEquals(playerA.rating() + playerB.rating(), result.ratingA() + result.ratingB(), "Total rating preserved");
    }

    @Test
    @DisplayName("Invalid match scores throw IllegalArgumentException")
    void invalidScoreThrows() {
        Elo.Player playerA = new Elo.Player(1200, 0);
        Elo.Player playerB = new Elo.Player(1200, 0);

        assertThrows(IllegalArgumentException.class, () -> Elo.play(playerA, playerB, 0.75));
    }
}
