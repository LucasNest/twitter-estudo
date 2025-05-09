package tech.buildrun.springsecurity.controller.dto;

import tech.buildrun.springsecurity.entities.UserAppearence;

public record UserCustomizationDto(String themeColor,
                                   String bio,
                                   String profileImageUrl,
                                   String backgroundImageUrl,
                                   Boolean darkMode) {
}
