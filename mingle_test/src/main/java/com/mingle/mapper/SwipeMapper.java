package com.mingle.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mingle.vo.SwipeVO;

@Mapper
public interface SwipeMapper {

    int insertSwipe(SwipeVO swipe);
    
    boolean existsLike(
    	    @Param("swiperId") int swiperId,
    	    @Param("targetId") int targetId
    	);
    
    boolean existsSwipe(
    	    @Param("swiperId") int swiperId,
    	    @Param("targetId") int targetId
    	);

    // 오늘 보낸 슈퍼 좋아요 수 (하루 제한 확인용)
    int countTodaySuperLikes(@Param("swiperId") int swiperId);
}