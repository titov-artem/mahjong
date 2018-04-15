package com.github.mahjong.league.model;

import com.github.mahjong.common.enums.LangIso639;
import lombok.Data;

import java.util.*;

@Data
public class League {

    private final long id;
    private final Map<LangIso639, String> name;
    private final Map<LangIso639, String> description;
    private final Set<Long> admins;

    public League withAdmin(Long adminId) {
        Set<Long> admins = new HashSet<>(this.admins);
        admins.add(adminId);
        return new League(
                id, name, description, admins
        );
    }
}
