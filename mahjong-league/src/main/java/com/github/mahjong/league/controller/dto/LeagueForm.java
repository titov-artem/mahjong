package com.github.mahjong.league.controller.dto;

import com.github.mahjong.api.common.LangIso639Dto;
import com.github.mahjong.common.enums.EnumUtils;
import com.github.mahjong.common.enums.LangIso639;
import com.github.mahjong.league.model.League;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public class LeagueForm {

    @Nullable
    public Long id;
    @NotNull
    @NotEmpty
    public Map<LangIso639Dto, String> name;
    @NotNull
    @NotEmpty
    public Map<LangIso639Dto, String> description;
    @NotNull
    @NotEmpty
    public Set<Long> admins;

    public League toLeague() {
        return new League(
                id == null ? -1 : id,
                toLangIsoMap(name),
                toLangIsoMap(description),
                admins
        );
    }

    private Map<LangIso639, String> toLangIsoMap(Map<LangIso639Dto, String> map) {
        return map.entrySet().stream()
                .collect(toMap(
                        e -> EnumUtils.transferClass(e.getKey(), LangIso639.class),
                        Map.Entry::getValue
                ));
    }
}
