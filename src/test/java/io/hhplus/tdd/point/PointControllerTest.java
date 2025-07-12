package io.hhplus.tdd.point;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private void 충전(long userId, long amount) throws Exception {
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk());
    }

    private void 사용(long userId, long amount) throws Exception {
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isOk());
    }

    @Test
    void 포인트_조회_동작() throws Exception {
        long userId = 1L;

        // REFACTOR 제외, 주요 로직
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }


    @Test
    void 포인트_충전_정상_동작() throws Exception {
        long userId = 2L;
        long chargeAmount = 3_000L;

        // REFACTOR 제외, 주요 로직
        // 포인트 충전 테스트를 먼저 작성하였으나, 기존 코드에 new UserPoint()가 고정되어있어 isOk()만으로는 확인 불가
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chargeAmount)))
                .andExpect(status().isOk());

        // REFACTOR 제외, 검증 로직
        // 포인트 조회로 id와 포인트를 확인하기 위해 controller의 GET /point/{id} 우선 구현(연결) 필요 확인
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId)) // GET /point/{id} 구현 후 id는 통과, point는 실패
                .andExpect(jsonPath("$.point").value(chargeAmount));
    }

    @Test
    void 포인트_충전_실패_0보다_작은_금액() throws Exception {
        long userId = 3L;
        long chargeAmount = -500L;

        // REFACTOR 제외, 주요 로직
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargeAmount)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void 포인트_충전_실패_최대_제한_금액() throws Exception {
        long userId = 4L;
        long chargeAmount = 100_010L;

        // REFACTOR 제외, 주요 로직
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargeAmount)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void 포인트_사용_성공() throws Exception {
        long userId = 5L;
        long chargeAmount = 5_000L;
        long useAmount = 2_000L;
        long expectedRemaining = chargeAmount - useAmount;

        // 먼저 충전, REFACTOR
        충전(userId, chargeAmount);

        // 포인트 사용, REFACTOR 제외, 주요 로직
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(useAmount)))
                .andExpect(status().isOk());

        // 남은 포인트 확인, REFACTOR 제외, 검증 로직
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(expectedRemaining));
    }

    @Test
    void 포인트_사용_실패_포인트_부족() throws Exception {
        long userId = 6L;
        long chargeAmount = 1_000L;
        long useAmount = 5_000L;

        // 먼저 충전, REFACTOR
        충전(userId, chargeAmount);

        // 사용 실패, REFACTOR 제외, 주요 로직
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(useAmount)))
                .andExpect(status().isInternalServerError()); // 500 Internal Server Error
    }

    @Test
    void 포인트_히스토리_조회() throws Exception {
        long userId = 7L;

        // REFACTOR
        충전(userId, 1000L);
        충전(userId, 1000L);
        사용(userId, 500L);
        충전(userId, 2000L);
        사용(userId, 1000L);

        // 히스토리 조회 및 검증, REFACTOR 제외, 주요 로직
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[0].amount").value(1000))
                .andExpect(jsonPath("$[1].type").value("CHARGE"))
                .andExpect(jsonPath("$[1].amount").value(1000))
                .andExpect(jsonPath("$[2].type").value("USE"))
                .andExpect(jsonPath("$[2].amount").value(500))
                .andExpect(jsonPath("$[3].type").value("CHARGE"))
                .andExpect(jsonPath("$[3].amount").value(2000))
                .andExpect(jsonPath("$[4].type").value("USE"))
                .andExpect(jsonPath("$[4].amount").value(1000));
    }

}
