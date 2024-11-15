package ch.unisg.omi.controller.dto;

import lombok.Getter;

public class RoleDTO {

    public static final String MEDIA_TYPE = "application/role+json";

    @Getter
    private String agentName;

    @Getter
    private String roleName;

    @Getter
    private String groupName;
}
