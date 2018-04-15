package com.github.mahjong.main.publicapi;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.main.publicapi.dto.statistic.CombinationDistributionView;
import com.github.mahjong.main.publicapi.dto.statistic.PlaceDistributionView;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/statistic")
public interface StatisticController extends REST {

    @Path("/place-distribution")
    @GET
    PlaceDistributionView getPlaceDistribution();

    @Path("/combination-distribution")
    @GET
    CombinationDistributionView getCombinationDistribution(@QueryParam("rules") @NotNull String rulesSetCode);

}
