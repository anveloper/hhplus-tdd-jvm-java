package io.hhplus.tdd.point;

import java.util.List;

import org.springframework.stereotype.Service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;

@Service
public class PointServiceImpl implements PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint charge(long userId, long amount) {
        long prevPoint = userPointTable.selectById(userId).point(); // 아이디로 이전 금액을 조회하고,

        UserPoint updated = userPointTable.insertOrUpdate(userId, prevPoint + amount); // 포인트를 업데이트 한다.
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, updated.updateMillis()); // 히스토리에 기록

        return updated; // 업데이트 된 포인트를 반환한다.
    }

    @Override
    public UserPoint use(long userId, long amount) {
        long currentPoint = userPointTable.selectById(userId).point(); // 아이디로 현재 금액을 조회하고,

        if (currentPoint < amount) { // 사용 금액과 비교하여, 부족하면 에러 처리
            throw new IllegalStateException("포인트가 부족합니다."); //
        }

        UserPoint updated = userPointTable.insertOrUpdate(userId, currentPoint - amount); // 포인트를 업데이트
        pointHistoryTable.insert(userId, amount, TransactionType.USE, updated.updateMillis()); // 히스토리 기록

        return updated; // 업데이트 된 포인트 반환
    }

    @Override
    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    @Override
    public List<PointHistory> getHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}

