package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SecretSantaService {

    public Map<Participant, Participant> assignGiftees(List<Participant> participants) {
        if (participants == null || participants.size() < 2) {
            // Handle cases with less than 2 participants (no valid secret santa)
            return Collections.emptyMap();
        }

        List<Participant> shuffledParticipants = new ArrayList<>(participants);
        Collections.shuffle(shuffledParticipants);

        Map<Participant, Participant> assignments = new HashMap<>();
        int n = participants.size();

        for (int i = 0; i < n; i++) {
            Participant gifter = shuffledParticipants.get(i);
            // Assign the next participant in the shuffled list as the giftee, wrapping
            // around for the last participant
            Participant giftee = shuffledParticipants.get((i + 1) % n);

            assignments.put(gifter, giftee);
        }

        return assignments;
    }
}