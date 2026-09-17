package com.mingle.util;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 트랜잭션 관련 공통 기능
 */
public final class TransactionUtils {

    private TransactionUtils() {
    }

    /**
     * 현재 트랜잭션이 커밋된 뒤 실행 (트랜잭션 밖에서 호출하면 바로 실행)
     *
     * WebSocket 알림처럼 "DB에 반영된 뒤에 알려야 하는 일"에 사용한다.
     * 커밋 전에 보내면 받은 쪽이 바로 조회했을 때 아직 DB에 없을 수 있다.
     *
     * 주의: task 안에서 DB를 조회하면 호출되는 메서드는
     * @Transactional(propagation = Propagation.REQUIRES_NEW)여야 한다.
     * (커밋이 끝난 트랜잭션에 섞여 들어가면 MyBatis 세션이 정리되지 않음)
     */
    public static void afterCommit(Runnable task) {

        if (TransactionSynchronizationManager.isSynchronizationActive()) {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });

        } else {
            task.run();
        }
    }
}
