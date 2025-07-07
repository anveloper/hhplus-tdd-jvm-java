package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void 포인트_충전_실패() {
        long userId = 1L;
        long amount = -1000L;

        assertThrows(IllegalArgumentException.class, () -> service.charge(userId, amount));
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

        // TODO: 포인트 충전 확인과 거의 비슷한 로직, 추후 테스트를 통합?
    }

    @Test
    void 포인트_히스토리_조회() {
        long userId = 1L;

        // 기록 생성
        service.charge(userId, 1000L);
        service.charge(userId, 1000L);
        service.use(userId, 500L);
        service.charge(userId, 2000L);
        service.use(userId, 1000L);

        List<PointHistory> historyList = service.getHistory(userId);

        assertEquals(5, historyList.size());

        assertEquals(TransactionType.CHARGE, historyList.get(0).type());
        assertEquals(1000L, historyList.get(0).amount());

        assertEquals(TransactionType.CHARGE, historyList.get(1).type());
        assertEquals(1000L, historyList.get(1).amount());

        assertEquals(TransactionType.USE, historyList.get(2).type());
        assertEquals(500L, historyList.get(2).amount());

        assertEquals(TransactionType.CHARGE, historyList.get(3).type());
        assertEquals(2000L, historyList.get(3).amount());

        assertEquals(TransactionType.USE, historyList.get(4).type());
        assertEquals(1000L, historyList.get(4).amount());

        UserPoint finalPoint = service.getUserPoint(userId);
        assertNotNull(finalPoint);
        assertEquals(2500L, finalPoint.point());

        // TODO: 최종 잔액 확인도 포인트 조회와 중복되는 테스트가 아닌 지?
    }
}
