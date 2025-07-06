package io.hhplus.tdd.point;

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

public interface PointService {
    UserPoint charge(long userId, long amount);

    UserPoint use(long userId, long amount);

    UserPoint getUserPoint(long userId);

    List<PointHistory> getHistory(long userId);
}
