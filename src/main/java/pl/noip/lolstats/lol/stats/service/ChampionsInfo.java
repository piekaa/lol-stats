package pl.noip.lolstats.lol.stats.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChampionsInfo {
    private Map<String, Champion> data;
}
