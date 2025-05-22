package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.facade.PointServiceFacade;
import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPointService userPointService;

    @MockBean
    private PointServiceFacade pointServiceFacade;

    @MockBean
    private PointHistoryService pointHistoryService;

    @MockBean
    private UserPointTable userPointTable;

    @Test
    @DisplayName("회원 포인트 충전")
    public void 회원_포인트_충전() throws Exception {
        //given
        long userId = 1l;
        long saveAmount = 600l;
        UserPoint userPoint = new UserPoint(userId, saveAmount, System.currentTimeMillis());
        when(userPointService.savePoint(userId, saveAmount)).thenReturn(userPoint);

        //when
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(saveAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(saveAmount));

        verify(userPointService).savePoint(userId, saveAmount);
    }

    @Test
    @DisplayName("회원 포인트 충전 - 이력 적재")
    public void 회원_포인트_충전_이력적재() throws Exception {
        //given
        long userId = 1l;
        long saveAmount = 600;
        UserPoint userPoint = new UserPoint(userId, saveAmount, System.currentTimeMillis());
        when(userPointService.savePoint(userId, saveAmount)).thenReturn(userPoint);

        //when
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(saveAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(saveAmount));

        verify(userPointService).savePoint(userId, saveAmount);

    }

    @Test
    @DisplayName("회원 포인트 조회")
    public void 회원_포인트_조회() throws Exception {

        //given
        long userId = 1l;
        long originalPointAmount = 100l;
        UserPoint userPoint = new UserPoint(userId, originalPointAmount, System.currentTimeMillis());
        when(userPointService.getUserPoint(userId)).thenReturn(userPoint);

        //when
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk());

        verify(userPointService).getUserPoint(userId);

    }

    @Test
    @DisplayName("회원 포인트 이력 조회")
    public void 회원_포인트_이력_조회() throws Exception {

        //given
        long userId = 1l;
        List<PointHistory> histories = Arrays.asList(
                new PointHistory(1L, userId, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, 200L, TransactionType.USE, System.currentTimeMillis()),
                new PointHistory(3L, userId, 300L, TransactionType.CHARGE, System.currentTimeMillis())
        );

        when(pointHistoryService.findById(userId)).thenReturn(histories);

        //when
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // 배열인지 확인
                .andExpect(jsonPath("$.length()").value(3)) // 길이 확인
                .andExpect(jsonPath("$[0].amount").value(100L)) // 첫 번째 요소
                .andExpect(jsonPath("$[1].amount").value(200L))  // 두 번째 요소
                .andExpect(jsonPath("$[2].amount").value(300L)); // 세 번째 요소

    }

    @Test
    @DisplayName("회원 포인트 사용 정상")
    public void 회원_포인트_사용_정상() throws Exception {
        long userId = 1l;

        long originalPoint = 800;
        long usePoint = 300;
        long remainingPoint = 500;

        UserPoint originalUserPoint = new UserPoint(userId, originalPoint, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(originalUserPoint);

        UserPoint userPoint = new UserPoint(userId, remainingPoint, System.currentTimeMillis());
        when(userPointService.usePoint(userId, usePoint)).thenReturn(userPoint);

        //when
        mockMvc.perform(patch("/point/{id}/use", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.valueOf(usePoint))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(remainingPoint));

//        verify(userPointTable).selectById(userId);
        verify(userPointService).usePoint(userId, usePoint);
    }

    @Test
    @DisplayName("회원 포인트 사용 초과 에러")
    public void 회원_포인트_사용_초과_에러() throws Exception {
        long userId = 1l;

        long originalPoint = 800;
        long usePoint = 1000;
        long remainingPoint = 500;

        UserPoint originalUserPoint = new UserPoint(userId, originalPoint, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(originalUserPoint);

        UserPoint userPoint = new UserPoint(userId, remainingPoint, System.currentTimeMillis());
        when(userPointService.usePoint(userId, usePoint)).thenThrow(new IllegalArgumentException("에러가 발생했습니다."));

        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(usePoint)))
                .andDo(print()) // 에러 응답도 출력됨
                .andExpect(status().is5xxServerError())// 또는 적절한 상태 코드
                .andExpect(jsonPath("$.message").value("에러가 발생했습니다."));

//        verify(userPointTable).selectById(userId);
        verify(userPointService).usePoint(userId, usePoint);
    }

}