package com.example.blog_project.security;

import com.example.blog_project.domain.User;
import com.example.blog_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;


//실제로 시큐리티가 사용자의 검증을 하기 위해 사용할 유저 객체를 만들어 준다.
@Service // 빈으로 등록하는 과정
@RequiredArgsConstructor // 생성자 주입
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username + "의 사용자가 없습니다");
        }
        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(username); //시큐리티에서 제공하는 User객체를 사용해 username을 넣어준다.
        userBuilder.password(user.getPassword());//비밀번호넣어준다.
        userBuilder.roles(user.getRoles().stream().map(role -> role.getName()).toArray(String[]::new));//역할도 알려준다.
        return userBuilder.build();
    }
}

