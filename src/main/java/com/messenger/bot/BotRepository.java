package com.messenger.bot;

import com.messenger.bot.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BotRepository extends JpaRepository<Bot, UUID> {

    Optional<Bot> findByToken(String token);

    Optional<Bot> findByUserId(UUID userId);

    Optional<Bot> findByUsername(String username);

    List<Bot> findByOwnerId(UUID ownerId);

    boolean existsByUsername(String username);
}
