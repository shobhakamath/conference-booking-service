package com.conference.roomservice.exception;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorDTO(String code, List<String> messages) {}