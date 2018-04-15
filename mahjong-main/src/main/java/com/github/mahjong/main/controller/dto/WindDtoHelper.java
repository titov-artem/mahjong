package com.github.mahjong.main.controller.dto;

import com.github.mahjong.api.common.dto.WindDto;
import com.github.mahjong.main.model.Wind;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class WindDtoHelper {

    public static WindDto from(Wind wind) {
        return WindDto.valueOf(wind.name());
    }

    public static List<WindDto> getOrdered() {
        return Wind.getOrdered().stream().map(WindDtoHelper::from).collect(toList());
    }

    public static Wind toWind(WindDto dto) {
        return Wind.valueOf(dto.name());
    }
}
