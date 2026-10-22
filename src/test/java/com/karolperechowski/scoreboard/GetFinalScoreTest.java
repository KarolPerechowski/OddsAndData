package com.karolperechowski.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class GetFinalScoreTest {

    private final Scoreboard scoreboard = new Scoreboard();

    @Test
    void returnsEmptyWhenNoMatchHasFinished() {
        scoreboard.startMatch("Mexico", "Canada");

        assertTrue(scoreboard.getFinalScore().isEmpty());
    }

    @Test
    void recordsTheScoreAtTheMomentTheMatchFinishes() {
        scoreboard.startMatch("Spain", "Brazil");
        scoreboard.updateScore("Spain", "Brazil", 10, 2);
        scoreboard.finishMatch("Spain", "Brazil");

        assertEquals(List.of(new Match("Spain", "Brazil", 10, 2)), scoreboard.getFinalScore());
        assertTrue(scoreboard.getSummary().isEmpty());
    }

    @Test
    void ordersByTotalScoreThenHomeTeamThenAwayTeam() {
        finish("Mexico", "Canada", 0, 5);
        finish("Spain", "Brazil", 10, 2);
        finish("Germany", "France", 2, 2);
        finish("Uruguay", "Italy", 6, 6);
        finish("Argentina", "Australia", 3, 1);
        finish("Belgium", "Croatia", 2, 2);

        assertEquals(
                List.of(
                        new Match("Spain", "Brazil", 10, 2),
                        new Match("Uruguay", "Italy", 6, 6),
                        new Match("Mexico", "Canada", 0, 5),
                        new Match("Argentina", "Australia", 3, 1),
                        new Match("Belgium", "Croatia", 2, 2),
                        new Match("Germany", "France", 2, 2)),
                scoreboard.getFinalScore());
    }

    @Test
    void breaksRemainingTiesByAwayTeamName() {
        finish("Mexico", "Spain", 1, 1);
        finish("Mexico", "Canada", 2, 0);

        assertEquals(
                List.of(
                        new Match("Mexico", "Canada", 2, 0),
                        new Match("Mexico", "Spain", 1, 1)),
                scoreboard.getFinalScore());
    }

    @Test
    void keepsEarlierFinalsWhenTheSameTeamsPlayAgain() {
        finish("Mexico", "Canada", 0, 1);
        scoreboard.startMatch("Mexico", "Canada");
        scoreboard.updateScore("Mexico", "Canada", 2, 2);

        assertEquals(List.of(new Match("Mexico", "Canada", 0, 1)), scoreboard.getFinalScore());
        assertEquals(List.of(new Match("Mexico", "Canada", 2, 2)), scoreboard.getSummary());
    }

    private void finish(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        scoreboard.startMatch(homeTeam, awayTeam);
        scoreboard.updateScore(homeTeam, awayTeam, homeScore, awayScore);
        scoreboard.finishMatch(homeTeam, awayTeam);
    }
}
