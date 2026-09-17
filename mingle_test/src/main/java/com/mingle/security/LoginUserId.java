package com.mingle.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 로그인한 회원 ID를 컨트롤러 파라미터로 받는다.
 *
 * <pre>
 * public ResponseEntity&lt;?&gt; getChats(&#64;LoginUserId int userId) { ... }
 * </pre>
 *
 * 값은 Spring Security가 들고 있는 로그인 정보(LoginUser)에서 꺼낸다.
 * (servlet-context.xml에 LoginUserIdArgumentResolver 등록)
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUserId {
}
