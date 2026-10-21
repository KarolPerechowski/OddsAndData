package com.karolperechowski.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FindMatchByTeamTest {

    private final Scoreboard scoreboard = new Scoreboard();

    @Test
    void findsMatchByHomeOrAwayTeam() {
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore("Mexico", "Canada", 0, 5);

        Match expected = new Match("Mexico", "Canada", 0, 5);
        assertEquals(expected, scoreboard.findMatchByTeam("Mexico").orElseThrow());
        assertEquals(expected, scoreboard.findMatchByTeam("Canada").orElseThrow());
    }

    @Test
    void returnsEmptyWhenTeamIsNotPlaying() {
        scoreboard.startMatch("Mexico", "Canada");

        assertTrue(scoreboard.findMatchByTeam("Spain").isEmpty());
    }

    @Test
    void returnsEmptyAfterTheMatchFinishes() {
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.finishMatch("Mexico", "Canada");

        assertTrue(scoreboard.findMatchByTeam("Mexico").isEmpty());
    }

    @Test
    void rejectsBlankTeamName() {
        assertThrows(IllegalArgumentException.class, () -> scoreboard.findMatchByTeam("  "));
    }
}
