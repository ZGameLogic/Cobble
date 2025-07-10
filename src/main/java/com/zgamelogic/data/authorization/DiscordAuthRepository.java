package com.zgamelogic.data.authorization;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscordAuthRepository extends JpaRepository<DiscordAuth, String> {
}
