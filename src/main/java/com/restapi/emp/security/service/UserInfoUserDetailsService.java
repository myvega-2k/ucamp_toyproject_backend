package com.restapi.emp.security.service;

import com.restapi.emp.security.model.UserInfo;
import com.restapi.emp.security.model.UserInfoRepository;
import com.restapi.emp.security.model.UserInfoUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserInfoRepository repository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> optionalUserInfo = repository.findByEmail(username);
        return //optionalUserInfo.map(userInfo -> new UserInfoUserDetails(userInfo))
                optionalUserInfo.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }

//    public String addUser(UserInfo userInfo) {
//        //password 암호화
//        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
//        //user_info 테이블에 Insert
//        UserInfo savedUserInfo = repository.save(userInfo);
//        return savedUserInfo.getName() + " user added!!";
//    }
}