package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
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
class PointHistoryServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Test
    @DisplayName("회원 포인트 내역 조회")
    public void 회원_포인트_내역_조회() {
        when(pointHistoryTable.selectAllByUserId(1L)).thenReturn(Arrays.asList(
                new PointHistory(1L, 1L, 100, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, 1L, 200, TransactionType.CHARGE, System.currentTimeMillis())
        ));

        List<PointHistory> pointHistoryList = pointHistoryService.findById(1L);

        Assertions.assertThat(pointHistoryList.size()).isEqualTo(2);

    }
}