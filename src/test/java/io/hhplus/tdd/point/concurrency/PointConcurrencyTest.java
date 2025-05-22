package io.hhplus.tdd.point.concurrency;

import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.UserPointService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    private UserPointService userPointService;

    @Test
    @DisplayName("서비스 계층 동시성 테스트")
    void 서비스_계층_동시성_테스트() throws InterruptedException {
        // Given
        Long userId = 1L;

        int threadCount = 100;
        Long saveAmount = 50L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When - 서비스 메서드 직접 호출
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.savePoint(userId, saveAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        UserPoint finalPoint = userPointService.getUserPoint(userId);
        Long expectedPoint = threadCount * saveAmount;

        Assertions.assertThat(finalPoint.point()).isEqualTo(expectedPoint);

        System.out.println("서비스 테스트 - 성공: " + successCount.get() + ", 실패: " + failCount.get());
        System.out.println("최종 포인트: " + finalPoint.point() + " (예상: " + expectedPoint + ")");
    }
}
