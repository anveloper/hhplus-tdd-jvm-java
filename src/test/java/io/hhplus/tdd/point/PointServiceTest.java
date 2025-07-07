package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointServiceTest {
    private final UserPointTable userPointTable = new UserPointTable();
    private final PointHistoryTable pointHistoryTable = new PointHistoryTable();
    private final PointService service = new PointServiceImpl(userPointTable, pointHistoryTable);

    @Test
    void 포인트_충전_성공() {
        long userId = 1L;
        long amount = 1000L;

        UserPoint result = service.charge(userId, amount);

        assertNotNull(result); // UserPoint 객체가 정상적으로 생성되고,
        assertEquals(userId, result.id()); // 생성된 아이디와 주입한 아이디가 동일한 지,
        assertEquals(amount, result.point()); // 금액은 알맞게 충전 되었는 지,
    }

    @Test
    void 포인트_사용_성공() {
        long userId = 1L;
        long initialAmount = 2000L;
        long useAmount = 1000L;

        service.charge(userId, initialAmount);

        UserPoint result = service.use(userId, useAmount);

        assertNotNull(result); // 포인트가 정상적으로 생성되었는 지,
        assertEquals(userId, result.id());
        assertEquals(initialAmount - useAmount, result.point()); // 차액이 올바른 지
    }

    @Test
    void 포인트_사용_실패() {
        long userId = 1L;
        long amount = 2000L;

        assertThrows(IllegalStateException.class, () -> service.use(userId, amount));
    }

    @Test
    void 포인트_조회() {
        long userId = 1L;
        long amount = 1000L;

        service.charge(userId, amount); // 먼저 금액을 충전

        UserPoint result = service.getUserPoint(userId); // 포인트 조회 함수 호출

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(amount, result.point()); // 포인트 확인

        // TODO: 포인트 충전 확인과 거의 비슷한 로직이므로, 추후 테스트를 통합?
    }

    @Test
    void 포인트_히스토리_조회() {

    }
}
