package com.github.mahjong.security.service;

import com.github.mahjong.common.security.api.model.MahjongUserRole;
import com.github.mahjong.security.model.MahjongUser;
import com.github.mahjong.security.repo.UserRepo;
import com.github.mahjong.common.security.api.model.MahjongUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class JdbcUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public JdbcUserDetailsService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MahjongUser> userOpt = userRepo.get(username);
        if (!userOpt.isPresent()) {
            return toUserDetails(username, passwordEncoder.encode(""), MahjongUserRole.ANONYMOUS);
        }
        MahjongUser user = userOpt.get();
        return toUserDetails(user.getLogin(), user.getPassword(), MahjongUserRole.USER);
    }

    private UserDetails toUserDetails(String username,
                                      String password,
                                      MahjongUserRole role) {
        return new MahjongUserDetails(username, password, role);
    }

}
