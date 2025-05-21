package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    @DisplayName("회원 포인트 조회")
    public void 회원_포인트_조회() {
        UserPoint expectedUserPoint = new UserPoint(1L, 300, System.currentTimeMillis());
        when(userPointService.getUserPoint(1L)).thenReturn(expectedUserPoint);

        UserPoint userPoint = userPointService.getUserPoint(1L);
        Assertions.assertThat(userPoint.point()).isEqualTo(300);
    }

    @Test
    @DisplayName("회원 포인트 정상 사용 - 이력 x")
    public void 회원_포인트_정상_사용() {

        // Mock 반환값 설정
        UserPoint expectedUserPoint = new UserPoint(1L, 300, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(expectedUserPoint);

        UserPoint expectedUserPoint2 = new UserPoint(1L, 400, System.currentTimeMillis());
        when(userPointTable.insertOrUpdate(1L, 400)).thenReturn(expectedUserPoint2);

        // 메서드 호출
        UserPoint point = userPointService.savePoint(1L, 100);

        // 검증
        Assertions.assertThat(point.point()).isEqualTo(400);

    }

    @Test
    @DisplayName("포인트 부족 시 예외 발생")
    void throwExceptionWhenPointNotEnough() {
        // Given: 사용자가 200 포인트를 보유
        UserPoint userPoint = new UserPoint(1L, 200, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(userPoint);

        // When & Then: 300 포인트 사용 시도 시 예외 발생
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePoint(1L, 300);
        });
    }
}