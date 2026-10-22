# Live Football World Cup Scoreboard

A small Java 17 library that tracks World Cup matches: start a match, update the score, finish a match, and return a ranked summary of games still in progress. Finished matches stay available as final scores.

This repository is submitted as it would be for an internal code review. How AI tools were used is documented in [AI.md](AI.md).

## Build

Requires JDK 17+. The Maven Wrapper downloads Maven on first use.

```bash
./mvnw test          # macOS / Linux
mvnw.cmd test        # Windows
```

Library code lives in `com.karolperechowski.scoreboard`.

## API

```java
Scoreboard scoreboard = new Scoreboard();
scoreboard.startMatch("Mexico", "Canada");          // 0-0
scoreboard.updateScore("Mexico", "Canada", 0, 5);   // replaces the score
scoreboard.finishMatch("Mexico", "Canada");
List<Match> inProgress = scoreboard.getSummary();
List<Match> finals = scoreboard.getFinalScore();
```

`getSummary()` returns matches ordered by:

1. Total score (home + away), descending
2. If totals are tied, the match that started most recently first

`getFinalScore()` returns finished matches ordered by:

1. Total score, descending
2. If totals are tied, home team name A–Z (case-sensitive)
3. If the home team is also the same, away team name A–Z

## Assumptions

- This is an in-memory library, not a service. There is no persistence, HTTP API, or clock-based match duration.
- A match is identified by the home and away team names (trimmed). Names are case-sensitive.
- A new match always starts at 0-0. `updateScore` sets the current totals rather than adding a single goal.
- Scores are non-negative integers. There is no upper bound (World Cup games can go to extra time).
- A team may play in at most one in-progress match. Home and away must be different teams.
- Finishing a match removes it from the live board and stores that score for `getFinalScore()`. A team may start another match afterwards. The same pair can finish more than once; each final score is kept.
- "Most recently started" uses an internal start order, not wall-clock time, so ordering is stable even if two matches start in the same millisecond.
- Invalid input fails fast with `IllegalArgumentException` (or `NullPointerException` for null team names).

## Reasoning

The public surface is a single `Scoreboard` plus an immutable `Match` snapshot. Callers never mutate a `Match`; they ask the scoreboard to change state and then read a new snapshot. That keeps ranking logic in one place and makes tests straightforward.

Team names are the match key because the brief describes a live football board, not a generic tournament engine with generated IDs. Start order is stored as a monotonic counter so the tie-break is deterministic and independent of `System.nanoTime()`.

The board is synchronized. The data set is tiny (dozens of matches at most), so a single lock is simpler and safer than a concurrent collection plus extra invariants around "a team plays once".

## Trade-offs

- **In-memory only.** Easy to test and review; not suitable as a production store without a backing service.
- **Replace score vs increment.** Replacing totals matches how a scoreboard operator types "10-2". An increment API would be nicer for a feed of individual goals, but it is easier to get wrong if events are retried.
- **No match IDs.** Names are readable in tests and at the call site. The cost is that two different games with the same pair of names cannot overlap (already forbidden) and renaming a team is not supported.
- **Unchecked exceptions.** Invalid calls are programmer errors; checked exceptions would add noise without recovery paths.
- **No dependency besides JUnit.** The domain is small enough that a framework would hide the interesting code.

## Extra feature: final scores

`List<Match> getFinalScore()` returns every match that has finished, with the score it had at the moment it finished.

The required summary only answers "what is live right now?". Once a game ends it disappears from that list, and a results page or a recap still needs those scores. Ordering by total goals keeps the biggest games at the top, the same idea as the live summary. The tie-break is alphabetical by home team (then away team) because finish time is not part of the question being asked: two 2–2 games should line up by name, not by which one happened to be closed first.
