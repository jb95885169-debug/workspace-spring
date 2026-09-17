package com.mingle.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * &#64;LoginUserId가 붙은 int 파라미터에 로그인 회원 ID를 넣어 준다.
 *
 * 예전에는 컨트롤러마다 (int) session.getAttribute("userId")로 꺼냈는데,
 * 값이 없으면 NullPointerException으로 500이 났다.
 * 여기서는 로그인 정보가 없으면 Security가 로그인 화면으로 보내도록 예외를 던진다.
 */
public class LoginUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.hasParameterAnnotation(LoginUserId.class)
                && (int.class.equals(parameter.getParameterType())
                        || Integer.class.equals(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            // security-context.xml에서 이미 막지만, 설정이 바뀌어도 값이 비어 들어가지 않게 한다
            throw new InsufficientAuthenticationException("로그인이 필요합니다.");
        }

        return ((LoginUser) authentication.getPrincipal()).getUserId();
    }
}
