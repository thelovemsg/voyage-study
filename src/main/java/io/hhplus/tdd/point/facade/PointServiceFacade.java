package io.hhplus.tdd.point.facade;

import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceFacade {

    private final PointHistoryService pointHistoryService;
    private final UserPointService userPointService;

    //회원 포인트 충전
    public UserPoint chargePoint(long userId, int pointAmount) {
        UserPoint chargedUserPoint = userPointService.savePoint(userId, pointAmount);

        pointHistoryService.addPointHistory(userId, pointAmount, TransactionType.CHARGE);

        return chargedUserPoint;
    }

    //회원 포인트 사용
    public UserPoint usePoint(long userId, int pointAmount) {
        UserPoint usedUserPoint = userPointService.usePoint(userId, pointAmount);

        pointHistoryService.addPointHistory(userId, pointAmount, TransactionType.USE);

        return usedUserPoint;
    }

}
