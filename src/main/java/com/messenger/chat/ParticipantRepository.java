package com.messenger.chat;

import com.messenger.chat.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<ConversationParticipant, UUID> {

    @Query("SELECT DISTINCT p.user.id FROM ConversationParticipant p " +
           "WHERE p.conversation.id IN (SELECT c.conversation.id FROM ConversationParticipant c WHERE c.user.id = :userId) " +
           "AND p.user.id != :userId")
    List<UUID> findContactIds(@Param("userId") UUID userId);

    @Query("SELECT cp FROM ConversationParticipant cp JOIN FETCH cp.user WHERE cp.conversation.id = :conversationId AND cp.user.id = :userId")
    Optional<ConversationParticipant> findByConversationIdAndUserId(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId);

    @Query("SELECT cp FROM ConversationParticipant cp JOIN FETCH cp.user WHERE cp.conversation.id = :conversationId")
    List<ConversationParticipant> findAllByConversationId(@Param("conversationId") UUID conversationId);

    @Query("SELECT COUNT(cp) FROM ConversationParticipant cp WHERE cp.conversation.id = :conversationId")
    long countByConversationId(@Param("conversationId") UUID conversationId);
}
