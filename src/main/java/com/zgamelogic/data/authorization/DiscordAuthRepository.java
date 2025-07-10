package com.zgamelogic.data.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiscordAuthRepository extends JpaRepository<DiscordAuth, UUID> {
    List<DiscordAuth> findByDiscordTokenExpirationBetween(LocalDateTime start, LocalDateTime end);
    Optional<DiscordAuth> findByRollingToken(String rollingToken);
    List<DiscordAuth> findAllByRollingTokenExpirationBefore(LocalDateTime expiration);
}
