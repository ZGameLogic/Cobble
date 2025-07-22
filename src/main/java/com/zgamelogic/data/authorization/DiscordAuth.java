package com.zgamelogic.data.authorization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "discord_auth")
@AllArgsConstructor
@NoArgsConstructor
public class DiscordAuth {
    @Id
    @GeneratedValue
    private UUID id;
    private String rollingToken;
    private LocalDateTime rollingTokenExpiration;
    private String discordToken;
    private LocalDateTime discordTokenExpiration;
    private String discordRefreshToken;
}
