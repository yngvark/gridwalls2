package com.yngvark.gridwalls.microservices.zombie;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Config {
    private final String brokerHostname;
}
