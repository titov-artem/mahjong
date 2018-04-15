package com.github.mahjong.main.publicapi;

import com.github.mahjong.api.common.LangIso639Dto;
import com.github.mahjong.api.common.REST;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/enums")
public interface EnumsController extends REST {

    @Path("/langs")
    @GET
    List<LangIso639Dto> getSupportedLangs();

}
