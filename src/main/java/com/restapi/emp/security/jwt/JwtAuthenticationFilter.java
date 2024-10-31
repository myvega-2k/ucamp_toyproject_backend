package com.restapi.emp.security.jwt;

import com.restapi.emp.security.service.UserInfoUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //Authorization 헤더 값을 가져오기
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        //Authorization 헤더가 존재하고 , 그 헤더가 Bearer 로 시작하나요?
        //Bearer eyJhbGciOiJIUzI1NiJ9.e
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //token 문자열
            token = authHeader.substring(7);
            //token 유효성 검증 요청하고 , 토큰에 포함된 subject(email 주소) 추출
            username = jwtService.extractUsername(token);
        }

        //추출한 email 주소가 있고,  Authentication 객체 존재 여부 체크하고
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                //SecurityContext에 Authentication 객체 저장
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}