package com.karolperechowski.scoreboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory live scoreboard for World Cup matches currently in progress.
 *
 * <p>Team names identify a match. A team may appear in at most one in-progress match.
 * All mutating operations are synchronized so a single instance is safe to share.
 */
public final class Scoreboard {

    private final Map<MatchKey, InProgressMatch> matches = new LinkedHashMap<>();
    private long nextStartOrder;

    /**
     * Starts a new match at 0-0.
     *
     * @return a snapshot of the newly started match
     * @throws IllegalArgumentException if a team name is blank, both teams are the same,
     *                                  or either team is already playing
     */
    public synchronized Match startMatch(String homeTeam, String awayTeam) {
        String home = requireTeamName(homeTeam, "homeTeam");
        String away = requireTeamName(awayTeam, "awayTeam");
        if (home.equals(away)) {
            throw new IllegalArgumentException("Home and away teams must be different");
        }
        if (isPlaying(home) || isPlaying(away)) {
            throw new IllegalArgumentException("A team cannot play in more than one match at a time");
        }
        MatchKey key = new MatchKey(home, away);
        InProgressMatch started = new InProgressMatch(home, away, 0, 0, nextStartOrder++);
        matches.put(key, started);
        return started.toMatch();
    }

    /**
     * Replaces the current score of an in-progress match.
     *
     * @throws IllegalArgumentException if team names are blank, scores are negative,
     *                                  or the match is not in progress
     */
    public synchronized void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        MatchKey key = requireExistingKey(homeTeam, awayTeam);
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores must be non-negative");
        }
        InProgressMatch current = matches.get(key);
        matches.put(key, current.withScore(homeScore, awayScore));
    }

    /**
     * Removes a match from the scoreboard.
     *
     * @throws IllegalArgumentException if the match is not in progress
     */
    public synchronized void finishMatch(String homeTeam, String awayTeam) {
        MatchKey key = requireExistingKey(homeTeam, awayTeam);
        matches.remove(key);
    }

    /**
     * Returns in-progress matches ordered by total score descending, then most recently started first.
     */
    public synchronized List<Match> getSummary() {
        List<InProgressMatch> ordered = new ArrayList<>(matches.values());
        ordered.sort(Comparator
                .comparingInt(InProgressMatch::totalScore)
                .reversed()
                .thenComparing(Comparator.comparingLong(InProgressMatch::startOrder).reversed()));
        List<Match> summary = new ArrayList<>(ordered.size());
        for (InProgressMatch match : ordered) {
            summary.add(match.toMatch());
        }
        return List.copyOf(summary);
    }

    private MatchKey requireExistingKey(String homeTeam, String awayTeam) {
        MatchKey key = new MatchKey(
                requireTeamName(homeTeam, "homeTeam"),
                requireTeamName(awayTeam, "awayTeam"));
        if (!matches.containsKey(key)) {
            throw new IllegalArgumentException("No in-progress match for " + key.homeTeam() + " vs " + key.awayTeam());
        }
        return key;
    }

    private boolean isPlaying(String team) {
        for (InProgressMatch match : matches.values()) {
            if (match.involves(team)) {
                return true;
            }
        }
        return false;
    }

    private static String requireTeamName(String team, String parameterName) {
        Objects.requireNonNull(team, parameterName);
        String trimmed = team.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(parameterName + " must not be blank");
        }
        return trimmed;
    }

    private record MatchKey(String homeTeam, String awayTeam) {
    }

    private record InProgressMatch(
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore,
            long startOrder
    ) {
        int totalScore() {
            return homeScore + awayScore;
        }

        boolean involves(String team) {
            return homeTeam.equals(team) || awayTeam.equals(team);
        }

        InProgressMatch withScore(int newHomeScore, int newAwayScore) {
            return new InProgressMatch(homeTeam, awayTeam, newHomeScore, newAwayScore, startOrder);
        }

        Match toMatch() {
            return new Match(homeTeam, awayTeam, homeScore, awayScore);
        }
    }
}
