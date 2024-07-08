package com.example.blog_project.controller;

import com.example.blog_project.domain.JwtBlacklist;
import com.example.blog_project.domain.RefreshToken;
import com.example.blog_project.domain.Role;
import com.example.blog_project.domain.User;
import com.example.blog_project.jwt.util.JwtTokenizer;
import com.example.blog_project.security.dto.UserLoginDto;
import com.example.blog_project.security.dto.UserLoginResponseDto;
import com.example.blog_project.service.JwtBlacklistService;
import com.example.blog_project.service.RefreshTokenService;
import com.example.blog_project.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

//@Controller
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final JwtBlacklistService jwtBlackListService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDto userLoginDto,
                                BindingResult bindingResult, HttpServletResponse response){
        System.out.println("들어오나*****************************************************************************");
        System.out.println(userLoginDto.getUsername());
        System.out.println(userLoginDto.getPassword());
        //username,password가 null일때..(정해진 형식에 맞지 않을때..)
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        //일단은 username과 password 값을 잘 받아왔다면..
        //우리 서비스에 가입한 사용자 인지...  요거 알아봐요..
        User user = userService.findUserByUsername(userLoginDto.getUsername());
        //요청정보에서 얻어온 비밀번호와 우리 서비스가 갖고있는 비밀번호가 일치하는지확인.

        if(!passwordEncoder.matches(userLoginDto.getPassword(),user.getPassword())) {
            //비밀번호가 일치하지않을때!!
            return new ResponseEntity("비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED);
        }
        //여기까지 왔다는것은..  유저도있고, 비밀번호도 맞다.
        //롤객체를 꺼내서 롤의 이름만 리스트로 얻어온다.
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        //로그아웃 할 떄 refreshToken이 삭제가 되지 않았을 경우를 대비해 로그인 할 떄 기존의 refreshToken을 제거해준다.(이중장치 느낌)
        refreshTokenService.deleteRefreshToken(user.getId());

        //토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);
        //리프레시토큰을 디비에 저장.
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.getId());

        refreshTokenService.addRefreshToken(refreshTokenEntity);

        //응답으로 보낼 값들을 준비해요.
        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .build();

        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);  //보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
    }
    @GetMapping("/authtest")
    public String authTest(){
        return "authTest";
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response){
        //할일!!
        //1. 쿠키로부터 리프레시토큰을 얻어온다.
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        //2-1. 없다.  (오류로  응담.
        if(refreshToken == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //2-2. 있을때.
        //3. 토큰으로부터 정보를얻어와요.
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf ((Integer)claims.get("userId"));

        User user = userService.getUser(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));

        //4. accessToken 생성
        List roles = (List)claims.get("roles");


        String accessToken = jwtTokenizer.createAccessToken(userId, user.getEmail(),
                user.getName(), user.getUsername(), roles);

        //5. 쿠키 생성 response로 보내고.
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact( JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        response.addCookie(accessTokenCookie);

        // 6. 적절한 응답결과(ResponseEntity)를 생성해서 응답.
        UserLoginResponseDto responseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .userId(user.getId())
                .build();


        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public void logout(@CookieValue(name = "accessToken", required = false) String accessToken, @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        System.out.println("로그아웃 들어왔나");
        if (accessToken == null) {
            // accessToken이 존재하지 않으면 로그인되지 않은 상태로 간주하고 처리할 수 있습니다.
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Access token not found in cookies.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        //JWT 토큰 추출
        String jwt = accessToken;
        System.out.println("jwt: " + jwt);

        // 토큰의 만료 시간 추출
        Date expirationTime = Jwts.parser()
                .setSigningKey(jwtTokenizer.getAccessSecret())
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();
        System.out.println("만료시간: " + expirationTime);

        // 블랙리스트에 토큰 저장
        JwtBlacklist blacklist = new JwtBlacklist(jwt, expirationTime);
        jwtBlackListService.save(blacklist);

        // SecurityContext를 클리어하여 현재 세션을 무효화
        SecurityContextHolder.clearContext();

        // accessToken 쿠키 삭제
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        // refreshToken 쿠키 삭제
        Cookie refresCcookie = new Cookie("refreshToken", null);
        refresCcookie.setPath("/");
        refresCcookie.setMaxAge(0);
        response.addCookie(refresCcookie);

        //로그아웃 전 db에 저장되어있는 refreshToken을 삭제한다.
        refreshTokenService.deleteRefreshToken(refreshToken);

        // /signIn 페이지로 리디렉션
        try {
            response.sendRedirect("/signIn");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}