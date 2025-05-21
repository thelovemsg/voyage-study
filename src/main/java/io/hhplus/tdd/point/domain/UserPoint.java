package io.hhplus.tdd.point.domain;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public void isAmountEnoughToUse(int point) {
        if(this.point < point) throw new IllegalArgumentException("point is not enough");
    }
}
