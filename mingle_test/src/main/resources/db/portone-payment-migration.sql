-- PortOne 서버 검증 결과와 중복 결제를 기록하기 위한 Oracle 마이그레이션
-- 운영 DB에서 한 번만 실행한다.
ALTER TABLE mingle_payments ADD (
    imp_uid      VARCHAR2(100),
    merchant_uid VARCHAR2(100)
);

CREATE UNIQUE INDEX uk_mingle_payments_imp_uid
    ON mingle_payments (imp_uid);