package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointServiceImpl implements PointService{

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointServiceImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint charge(long userId, long amount) {
        return null;
    }

    @Override
    public UserPoint use(long userId, long amount) {
        return null;
    }

    @Override
    public UserPoint getUserPoint(long userId) {
        return null;
    }

    @Override
    public List<PointHistory> getHistory(long userId) {
        return List.of();
    }
}
