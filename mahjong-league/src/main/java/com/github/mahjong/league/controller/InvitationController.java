package com.github.mahjong.league.controller;

import com.github.mahjong.api.common.REST;
import com.github.mahjong.league.controller.dto.InvitationForm;
import com.github.mahjong.league.controller.dto.InvitationView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/invitation")
public interface InvitationController extends REST {

    /**
     * @return list of invitations for current user
     */
    @GET
    List<InvitationView> getAll();

    @POST
    InvitationView create(@NotNull @Valid InvitationForm form);

    @Path("/accept")
    @POST
    void accept(@NotNull String code);

    @Path("/reject")
    @POST
    void reject(@NotNull String code);

}
