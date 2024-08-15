package com.t13max.ai;


import com.t13max.util.func.Applier;
import lombok.Getter;
import lombok.Setter;

/**
 * 黑板共享数据
 *
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
@Setter
@Getter
public abstract class BlackboardParam {

    protected Object value;

    abstract void update();

    abstract boolean isExpired();

    static class TimeBlackBoardParam extends BlackboardParam {

        long expireTime;

        TimeBlackBoardParam(Object value, int millis) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + millis;
        }

        @Override
        public void update() {

        }

        @Override
        public boolean isExpired() {
            return expireTime < System.currentTimeMillis();
        }
    }

    static class SnapBoardParam extends BlackboardParam {

        boolean isTick;

        SnapBoardParam(Object value) {
            this.value = value;
        }

        @Override
        void update() {
            isTick = true;
        }

        @Override
        boolean isExpired() {
            return isTick;
        }
    }

    static class FunctionalParam extends TimeBlackBoardParam {

        FunctionalParam(Applier value, int millis) {
            super(value, millis);
        }

        @Override
        public void update() {
            if (super.isExpired()) {
                ((Applier) value).apply();
            }
        }
    }
}
