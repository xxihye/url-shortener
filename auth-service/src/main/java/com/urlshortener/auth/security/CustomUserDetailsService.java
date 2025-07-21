package com.urlshortener.auth.security;

import com.urlshortener.admin.repository.AdminUserRepository;
import com.urlshortener.auth.enums.Role;
import com.urlshortener.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        return userRepository.findByUserNo(Long.parseLong(userName))
                             .map(user -> new UserPrincipal(user.getUserNo(),
                                 user.getPassword(),
                                 Role.ROLE_USER))
                             .orElseGet(() -> adminUserRepository.findByAdminNo(Long.parseLong(userName))
                                                                 .map(admin -> new UserPrincipal(admin.getAdminNo(),
                                                                     admin.getPassword(),
                                                                     Role.ROLE_ADMIN))
                                                                 .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."))
                             );
    }
}
