package com.karolperechowski.scoreboard;

import java.util.Objects;

/**
 * An in-progress football match with a current score.
 * Instances are immutable snapshots; updating a score produces a new snapshot from {@link Scoreboard}.
 */
public final class Match {

    private final String homeTeam;
    private final String awayTeam;
    private final int homeScore;
    private final int awayScore;

    Match(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public String homeTeam() {
        return homeTeam;
    }

    public String awayTeam() {
        return awayTeam;
    }

    public int homeScore() {
        return homeScore;
    }

    public int awayScore() {
        return awayScore;
    }

    public int totalScore() {
        return homeScore + awayScore;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Match match)) {
            return false;
        }
        return homeScore == match.homeScore
                && awayScore == match.awayScore
                && homeTeam.equals(match.homeTeam)
                && awayTeam.equals(match.awayTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, awayTeam, homeScore, awayScore);
    }

    @Override
    public String toString() {
        return homeTeam + " " + homeScore + " - " + awayScore + " " + awayTeam;
    }
}
