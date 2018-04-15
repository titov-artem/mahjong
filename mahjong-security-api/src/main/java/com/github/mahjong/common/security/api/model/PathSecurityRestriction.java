package com.github.mahjong.common.security.api.model;

import lombok.Data;

@Data
public class PathSecurityRestriction {

    private final String pathExpression;
    private final String requiredAccessFilter;

    public static PathSecurityRestriction hasRole(String pathExpression, String role) {
        return new PathSecurityRestriction(
                pathExpression,
                String.format("hasRole('%s')", role)
        );
    }
}
