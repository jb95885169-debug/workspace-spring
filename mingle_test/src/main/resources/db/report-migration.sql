-- 신고 제목과 상세 내용을 저장하기 위한 컬럼
ALTER TABLE mingle_reports ADD (
    title VARCHAR2(100),
    content VARCHAR2(1000)
);