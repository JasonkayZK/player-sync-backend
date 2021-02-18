package io.github.jasonkayzk.playersyncbackend.entity;

import io.github.jasonkayzk.playersyncbackend.consts.PlayState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayInfo {

    private PlayState type;

    private String message;

    private Double currentTime;

}
