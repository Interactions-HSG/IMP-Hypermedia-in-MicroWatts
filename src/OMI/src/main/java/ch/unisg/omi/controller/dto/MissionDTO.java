package ch.unisg.omi.controller.dto;

import lombok.Getter;

public class MissionDTO {

    public static final String MEDIA_TYPE = "application/mission+json";

    @Getter
    private String agentName;

    @Getter
    private String missionName;

    @Getter
    private String schemeName;
}
