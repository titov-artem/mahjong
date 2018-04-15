package com.github.mahjong.main.publicapi;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.main.publicapi.dto.CombinationView;
import com.github.mahjong.main.publicapi.dto.RulesSetView;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/rules")
public interface RulesController extends REST {

    @GET
    List<RulesSetView> getAll();

    @Path("/{rulesSetCode}")
    @GET
    RulesSetView get(@PathParam("rulesSetCode") @NotEmpty String rulesSetCode);

    @Path("/combinations")
    @GET
    List<CombinationView> getAllCombinations(@QueryParam("rules") @NotNull String rulesSetCode);

}
