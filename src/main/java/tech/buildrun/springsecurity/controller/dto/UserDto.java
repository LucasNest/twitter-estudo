package tech.buildrun.springsecurity.controller.dto;

import java.util.List;

public record UserDto(List<SimpleUserDto> simpleUsers,
                      int page,
                      int pageSize,
                      int totalPages,
                      long totalElements) {
}
