package com.zgamelogic.data.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DiscordAuthRepository extends JpaRepository<DiscordAuth, String> {
    List<DiscordAuth> findByDiscordTokenExpirationBetween(LocalDateTime start, LocalDateTime end);
}
