package com.github.mahjong.main.app;

import com.github.mahjong.application.context.ThreadLocalUserContextSupport;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.common.security.api.model.MahjongUserRole;
import com.github.mahjong.main.model.Player;
import com.github.mahjong.main.repo.PlayerRepo;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserContextFillerInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger log = LoggerFactory.getLogger(UserContextFillerInterceptor.class);

    private final PlayerRepo playerRepo;
    private final ThreadLocalUserContextSupport contextSupport;

    @Inject
    public UserContextFillerInterceptor(PlayerRepo playerRepo,
                                        ThreadLocalUserContextSupport contextSupport) {
        super(Phase.RECEIVE);
        this.playerRepo = playerRepo;
        this.contextSupport = contextSupport;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        initUserLang();
    }

    // it is forbidden to throw exception from this method!
    private void initUserLang() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            contextSupport.initUserLang(LangIso639.EN);
            return;
        }
        boolean isUser = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (Objects.equals(authority.getAuthority(), MahjongUserRole.USER.name())) {
                isUser = true;
                break;
            }
        }
        if (!isUser || !(auth.getPrincipal() instanceof UserDetails)) {
            contextSupport.initUserLang(LangIso639.EN);
            return;
        }
        String login = ((UserDetails) auth.getPrincipal()).getUsername();
        Optional<Player> player;
        try {
            player = playerRepo.getByLogin(login);
        } catch (RuntimeException e) {
            log.error("Failed to load authenticated user by login " + login, e);
            player = Optional.empty();
        }
        if (!player.isPresent()) {
            contextSupport.initUserLang(LangIso639.EN);
            return;
        }
        contextSupport.initUserLang(player.get().getLang());
    }

}
