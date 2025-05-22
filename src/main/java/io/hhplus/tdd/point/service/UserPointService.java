package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointTable userPointTable;

    public UserPoint savePoint(long userId, long pointAmount) {
        UserPoint foundUserPoint = userPointTable.selectById(userId);
        long newPointAmount = foundUserPoint.point() + pointAmount;
        return userPointTable.insertOrUpdate(userId, newPointAmount);
    }

    public UserPoint usePoint(long userId, long pointAmount) {
        UserPoint foundUserPoint = userPointTable.selectById(userId);
        foundUserPoint.validate(pointAmount);

        long newPointAmount = foundUserPoint.point() - pointAmount;
        return userPointTable.insertOrUpdate(userId, newPointAmount);
    }

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

}
