package com.zgamelogic.services;

import com.zgamelogic.data.authorization.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthService {
    private final String characters;
    private final SecureRandom random;
    private final int tokenLength;

    private final DiscordService discordService;

    private final DiscordAuthRepository discordAuthRepository;

    public AuthService(DiscordService discordService, DiscordAuthRepository discordAuthRepository){
        this.discordService = discordService;
        this.discordAuthRepository = discordAuthRepository;
        random = new SecureRandom();
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        tokenLength = 64;
    }

    public WebsocketAuthData authorizeWithCode(String code){
        DiscordToken discordToken = discordService.postForToken(code);
        DiscordUser discordUser = discordService.getUserFromToken(discordToken.access_token());
        LocalDateTime rollingTokenExpire = LocalDateTime.now().plusDays(30);
        LocalDateTime discordTokenExpire = LocalDateTime.now().plusSeconds(discordToken.expires_in());
        DiscordAuth authentication = new DiscordAuth(
            generateRollingToken(),
            rollingTokenExpire,
            discordToken.access_token(),
            discordTokenExpire,
            discordToken.refresh_token()
        );
        discordAuthRepository.save(authentication);
        return new WebsocketAuthData(
            authentication.getRollingToken(),
            discordUser.id(),
            discordUser.avatar(),
            discordUser.username()
        );
    }

    public WebsocketAuthData authorizeWithRollingToken(String token){
        DiscordAuth authData = discordAuthRepository.findById(token).orElseThrow(() -> new RollingTokenNotFoundException("Token not found"));
        DiscordUser discordUser = discordService.getUserFromToken(authData.getDiscordToken());
        discordAuthRepository.deleteById(authData.getRollingToken());
        LocalDateTime rollingTokenExpire = LocalDateTime.now().plusDays(30);
        authData.setRollingToken(generateRollingToken());
        authData.setRollingTokenExpiration(rollingTokenExpire);
        discordAuthRepository.save(authData);
        return new WebsocketAuthData(
            authData.getRollingToken(),
            discordUser.id(),
            discordUser.avatar(),
            discordUser.username()
        );
    }

    private String generateRollingToken(){
        StringBuilder sb = new StringBuilder(tokenLength);
        for (int i = 0; i < tokenLength; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Scheduled(cron = "0 0 * * * *")
    private void hourTasks(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusHours(1);
        List<DiscordAuth> updated = new ArrayList<>();
        for(DiscordAuth authData: discordAuthRepository.findByDiscordTokenExpirationBetween(now, expiration)) {
            try {
                DiscordToken newToken = discordService.refreshToken(authData.getDiscordRefreshToken());
                authData.setDiscordToken(newToken.access_token());
                authData.setDiscordRefreshToken(newToken.refresh_token());
                LocalDateTime discordTokenExpire = LocalDateTime.now().plusSeconds(newToken.expires_in());
                authData.setDiscordTokenExpiration(discordTokenExpire);
                updated.add(authData);
            } catch (DiscordTokenRefreshException e) {
                log.error("Unable to refresh token {}", authData.getDiscordRefreshToken());
                discordAuthRepository.deleteById(authData.getRollingToken());
            }
        }
        discordAuthRepository.saveAll(updated);
    }
}
