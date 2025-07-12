package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * - API 요구사항
 *   - PATCH `/point/{id}/charge` : 포인트를 충전한다.
 *   - PATCH `/point/{id}/use` : 포인트를 사용한다.
 *   - GET `/point/{id}` : 포인트를 조회한다.
 * - 기능 요구사항
 *   - GET `/point/{id}/histories/` : 포인트 내역을 조회한다.
 *   - 잔고가 부족할 경우, 초인트 사용은 실패해야한다.
 */

@Service
@RequiredArgsConstructor
public class PointService {
    public static final long LIMIT_MAX_POINT = 100_000L;

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Synchronized
    UserPoint charge(long userId, long amount){
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        } else if (amount > LIMIT_MAX_POINT) {
            throw new IllegalArgumentException("충전 금액은 100,000보다 작아야 합니다.");
        }

        long prevPoint = userPointTable.selectById(userId).point(); // 아이디로 이전 금액을 조회하고,

        UserPoint updated = userPointTable.insertOrUpdate(userId, prevPoint + amount); // 포인트를 업데이트 한다.
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, updated.updateMillis()); // 히스토리에 기록

        return updated; // 업데이트 된 포인트를 반환한다.
    };

    @Synchronized
    UserPoint use(long userId, long amount){
        long currentPoint = userPointTable.selectById(userId).point(); // 아이디로 현재 금액을 조회하고,

        if (currentPoint < amount) { // 사용 금액과 비교하여, 부족하면 에러 처리
            throw new IllegalStateException("포인트가 부족합니다."); //
        }

        UserPoint updated = userPointTable.insertOrUpdate(userId, currentPoint - amount); // 포인트를 업데이트
        pointHistoryTable.insert(userId, amount, TransactionType.USE, updated.updateMillis()); // 히스토리 기록

        return updated; // 업데이트 된 포인트 반환
    };

    UserPoint getUserPoint(long userId){
        return userPointTable.selectById(userId);
    };

    List<PointHistory> getHistory(long userId){
        return pointHistoryTable.selectAllByUserId(userId);
    };
}
