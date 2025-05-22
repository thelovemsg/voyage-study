package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryTable pointHistoryTable;

    //회원 포인트 정보 리스트 조회
    public List<PointHistory> findById(long userId) {
        return Optional.ofNullable(pointHistoryTable.selectAllByUserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
    }

    public void addPointHistory(long userId, int pointAmount, TransactionType type) {
        pointHistoryTable.insert(userId, pointAmount, type, System.currentTimeMillis());
    }
    
}
