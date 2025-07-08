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

    @Test
    void 포인트_조회_동작() throws Exception {
        long userId = 1L;

        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }


    @Test
    void 포인트_충전_정상_동작() throws Exception {
        long userId = 1L;
        long chargeAmount = 3_000L;

        // 포인트 충전 테스트를 먼저 작성하였으나, 기존 코드에 new UserPoint()가 고정되어있어 isOk()만으로는 확인 불가
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chargeAmount)))
                .andExpect(status().isOk());

        // 포인트 조회로 id와 포인트를 확인하기 위해 controller의 GET /point/{id} 우선 구현(연결) 필요 확인
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId)) // GET /point/{id} 구현 후 id는 통과, point는 실패
                .andExpect(jsonPath("$.point").value(chargeAmount));
    }

    @Test
    void 포인트_충전_실패_0보다_작은_금액() throws Exception {
        long userId = 1L;
        long chargeAmount = -500L;

        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargeAmount)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void 포인트_충전_실패_최대_제한_금액() throws Exception {
        long userId = 1L;
        long chargeAmount = 100_010L;

        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargeAmount)))
                .andExpect(status().is5xxServerError());
    }
}
