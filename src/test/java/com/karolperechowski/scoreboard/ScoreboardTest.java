package com.karolperechowski.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ScoreboardTest {

    private final Scoreboard scoreboard = new Scoreboard();

    @Test
    void startMatchBeginsAtZeroZero() {
        Match match = scoreboard.startMatch("Mexico", "Canada");

        assertEquals(new Match("Mexico", "Canada", 0, 0), match);
        assertEquals(List.of(match), scoreboard.getSummary());
    }

    @Test
    void updateScoreReplacesTotals() {
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.updateScore("Spain", "Brazil", 10, 2);

        assertEquals(List.of(new Match("Spain", "Brazil", 10, 2)), scoreboard.getSummary());
    }

    @Test
    void finishMatchRemovesItFromSummary() {
        scoreboard.startMatch("Germany", "France");
        scoreboard.finishMatch("Germany", "France");

        assertTrue(scoreboard.getSummary().isEmpty());
    }

    @Test
    void summaryOrdersByTotalScoreThenMostRecentlyStarted() {
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.startMatch("Germany", "France");
        scoreboard.startMatch("Uruguay", "Italy");
        scoreboard.startMatch("Argentina", "Australia");

        scoreboard.updateScore("Mexico", "Canada", 0, 5);
        scoreboard.updateScore("Spain", "Brazil", 10, 2);
        scoreboard.updateScore("Germany", "France", 2, 2);
        scoreboard.updateScore("Uruguay", "Italy", 6, 6);
        scoreboard.updateScore("Argentina", "Australia", 3, 1);

        assertEquals(
                List.of(
                        new Match("Uruguay", "Italy", 6, 6),
                        new Match("Spain", "Brazil", 10, 2),
                        new Match("Mexico", "Canada", 0, 5),
                        new Match("Argentina", "Australia", 3, 1),
                        new Match("Germany", "France", 2, 2)),
                scoreboard.getSummary());
    }

    @Test
    void trimsTeamNames() {
        scoreboard.startMatch("  Mexico ", " Canada");
        scoreboard.updateScore("Mexico", "Canada", 1, 0);

        assertEquals("Mexico", scoreboard.getSummary().get(0).homeTeam());
        assertEquals("Canada", scoreboard.getSummary().get(0).awayTeam());
    }

    @Test
    void rejectsBlankOrIdenticalTeams() {
        assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch(" ", "Canada"));
        assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch("Mexico", "Mexico"));
        assertThrows(NullPointerException.class, () -> scoreboard.startMatch(null, "Canada"));
    }

    @Test
    void rejectsTeamPlayingTwoMatches() {
        scoreboard.startMatch("Mexico", "Canada");

        assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch("Mexico", "Spain"));
        assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch("Spain", "Canada"));
    }

    @Test
    void rejectsUnknownMatchAndNegativeScores() {
        scoreboard.startMatch("Mexico", "Canada");

        assertThrows(IllegalArgumentException.class, () -> scoreboard.updateScore("Spain", "Brazil", 1, 0));
        assertThrows(IllegalArgumentException.class, () -> scoreboard.updateScore("Mexico", "Canada", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> scoreboard.finishMatch("Spain", "Brazil"));
    }
}
