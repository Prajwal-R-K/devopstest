package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GiftRequestRepository extends JpaRepository<GiftRequest, String> {
    List<GiftRequest> findByParticipant(Participant participant);
}
