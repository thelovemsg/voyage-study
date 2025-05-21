package io.hhplus.tdd.point.facade;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceFacadeTest {

    @Mock
    private UserPointService userPointService;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private PointServiceFacade pointServiceFacade;

    @Test
    @DisplayName("회원 포인트 충전 - 이력 생성 o")
    public void 회원_포인트_충전() {

        // 사용자 포인트 이력 세팅
        when(pointHistoryService.findById(1L)).thenReturn(Arrays.asList(
                new PointHistory(1L, 1L, 100, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, 1L, 200, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(3L, 1L, 300, TransactionType.CHARGE, System.currentTimeMillis())
        ));

        // 사용자 포인트 정보 세팅
        UserPoint expectedUserPoint = new UserPoint(1L, 600, System.currentTimeMillis());
        when(userPointService.savePoint(1L, 300)).thenReturn(expectedUserPoint);

        //사용자 포인트 확인
        UserPoint userPoint = pointServiceFacade.chargePoint(1L, 300);
        List<PointHistory> pointHistoryList = pointHistoryService.findById(1L);

        Assertions.assertThat(userPoint.point()).isEqualTo(600);
        Assertions.assertThat(pointHistoryList.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("회원 포인트 사용 - 이력 생성 o")
    public void 회원_포인트_사용() {

        // 사용자 포인트 이력 세팅
        when(pointHistoryService.findById(1L)).thenReturn(Arrays.asList(
                new PointHistory(1L, 1L, 100, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, 1L, 200, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(3L, 1L, 300, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(4L, 1L, 100, TransactionType.USE, System.currentTimeMillis())
        ));

        // 사용자 포인트 정보 세팅
        UserPoint expectedUserPoint = new UserPoint(1L, 500, System.currentTimeMillis());
        when(userPointService.usePoint(1L, 100)).thenReturn(expectedUserPoint);

        //사용자 포인트 확인
        UserPoint userPoint = pointServiceFacade.usePoint(1L, 100);
        List<PointHistory> pointHistoryList = pointHistoryService.findById(1L);

        Assertions.assertThat(userPoint.point()).isEqualTo(500);
        Assertions.assertThat(pointHistoryList.size()).isEqualTo(4);

    }

}