package com.messenger.chat;

import com.messenger.chat.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<ConversationParticipant, UUID> {

    @Query("SELECT DISTINCT p.user.id FROM ConversationParticipant p " +
           "WHERE p.conversation.id IN (SELECT c.conversation.id FROM ConversationParticipant c WHERE c.user.id = :userId) " +
           "AND p.user.id != :userId")
    List<UUID> findContactIds(@Param("userId") UUID userId);
}
