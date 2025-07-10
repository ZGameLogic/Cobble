package com.zgamelogic.data.authorization;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "discord_auth")
@AllArgsConstructor
@NoArgsConstructor
public class DiscordAuth {
    @Id
    @Setter
    private String rollingToken;
    private LocalDateTime RollingTokenExpiration;
    private String discordToken;
    private LocalDateTime discordTokenExpiration;
    private String discordRefreshToken;
}
