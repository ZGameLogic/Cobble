package com.zgamelogic.services;

import com.zgamelogic.data.authorization.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
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
        LocalDateTime discordTokenExpire = LocalDateTime.now().plus(discordToken.expires_in(), ChronoUnit.MILLIS);
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
        authData.setRollingToken(generateRollingToken());
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

    }
}
