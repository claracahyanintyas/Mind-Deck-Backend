package org.individualproject.flashcards.domain.classroomSession;

import lombok.Getter;
import org.individualproject.flashcards.domain.review.ReviewChoice;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ClassroomSession {
    private final String roomCode;
    private final Long deckId;
    private Long currentCardId;
    private boolean cardFlipped = false;

    private final Set<String> connectedUsernames = ConcurrentHashMap.newKeySet();

    private final Map<String, ReviewChoice> currentCardVotes = new ConcurrentHashMap<>();

    public ClassroomSession(String roomCode, Long deckId) {
        this.roomCode = roomCode;
        this.deckId = deckId;
    }

    public void addStudent(String username) {
        if (username != null) {
            this.connectedUsernames.add(username);
        }
    }

    public void removeStudent(String username) {
        if (username != null) {
            this.connectedUsernames.remove(username);
        }
    }

    public void submitVote(String username, ReviewChoice choice) {
        if (this.currentCardId != null && username != null && choice != null) {
            this.currentCardVotes.put(username, choice); // 👈 Allows overwriting/updating their vote
        }
    }

    public void changeCard(Long newCardId) {
        this.currentCardId = newCardId;
        this.cardFlipped = false; // 👈 Reset to false whenever a new card is loaded!
        this.currentCardVotes.clear();
    }

    public int getConnectedStudentCount() {
        return connectedUsernames.size();
    }

    public int getTotalVotesCount() {
        return currentCardVotes.size();
    }

    // Convert keys to pure Strings so the frontend JSON parser receives it smoothly
    public Map<ReviewChoice, Long> getCurrentVoteTally() {
        Map<ReviewChoice, Long> tally = new ConcurrentHashMap<>();

        // Initialize every choice using your exact production enum values
        for (ReviewChoice choice : ReviewChoice.values()) {
            tally.put(choice, 0L);
        }

        currentCardVotes.values().forEach(choice -> {
            tally.put(choice, tally.get(choice) + 1);
        });

        return tally;
    }
    public void flipCard() {
        if (this.currentCardId != null) {
            this.cardFlipped = true;
        }
    }

}