package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPointService {

    private final UserPointTable userPointTable;
    ReentrantLock lock = new ReentrantLock();

    public UserPoint savePoint(long userId, long pointAmount) {
        lock.lock();
        try {
            log.info("saving point {} | {}", userId, pointAmount);
            UserPoint foundUserPoint = userPointTable.selectById(userId);
            long newPointAmount = foundUserPoint.point() + pointAmount;
            return userPointTable.insertOrUpdate(userId, newPointAmount);
        } finally {
            lock.unlock();
        }
    }

    public UserPoint usePoint(long userId, long pointAmount) {
        lock.lock();
        try {
            UserPoint foundUserPoint = userPointTable.selectById(userId);
            foundUserPoint.validate(pointAmount);

            long newPointAmount = foundUserPoint.point() - pointAmount;
            return userPointTable.insertOrUpdate(userId, newPointAmount);
        } finally {
            lock.unlock();
        }
    }

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

}
