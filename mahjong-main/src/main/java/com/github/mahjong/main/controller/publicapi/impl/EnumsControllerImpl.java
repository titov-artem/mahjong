package com.github.mahjong.main.controller.publicapi.impl;

import com.github.mahjong.api.common.LangIso639Dto;
import com.github.mahjong.main.publicapi.EnumsController;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Controller("publicEnumsController")
public class EnumsControllerImpl implements EnumsController {

    @Override
    public List<LangIso639Dto> getSupportedLangs() {
        List<LangIso639Dto> langs = Arrays.asList(LangIso639Dto.values());
        langs.sort(Comparator.comparing(LangIso639Dto::name));
        return langs;
    }

}
