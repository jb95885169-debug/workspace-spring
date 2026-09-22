-- 신고 처리 시 회원의 기간 정지 종료일을 저장한다.
ALTER TABLE mingle_users ADD suspended_until DATE;