package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.core.MapDimensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter
@EqualsAndHashCode
public class GameConfig {
    private MapDimensions mapDimensions;
    private int sleepTimeMillisBetweenTurns;
}
