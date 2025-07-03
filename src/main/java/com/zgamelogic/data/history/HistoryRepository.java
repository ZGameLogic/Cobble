package com.zgamelogic.data.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, UUID> {
    default List<History> findAllByPlayer_PlayerIdAndCompletedBetween(long playerId, LocalDate day){
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay().minusNanos(1);
        return findAllByPlayer_PlayerIdAndCompletedBetween(playerId, start, end);
    }

    List<History> findAllByPlayer_PlayerIdAndCompletedBetween(long playerPlayerId, LocalDateTime startDate, LocalDateTime endDate);
}
