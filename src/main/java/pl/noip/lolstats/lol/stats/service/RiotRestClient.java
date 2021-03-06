package pl.noip.lolstats.lol.stats.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.noip.lolstats.lol.stats.dto.stats.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class RiotRestClient {
    @Value("${riot.api.key}")
    private String key;

    @Value("${regions}")
    private String propertiesRegions;

    @Value("${number.of.matches.to.display}")
    private String numberOfMachtes;

    private RestTemplate restTemplate = new RestTemplate();
    private AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

    private HttpHeaders createHeaders(String apikey) {
        return new HttpHeaders() {{
            add("X-Riot-Token", apikey);
        }};
    }

    private String[] splitedRegions;

    @PostConstruct
    private void splitRegions() {

        splitedRegions = propertiesRegions.split(";");

    }

    public List<String> findSummonersRegions(String name) {
        List<String> regions = new ArrayList<>();

        HashMap<ListenableFuture<ResponseEntity<SummonerNameRequest>>, String> listenableFutures = new HashMap<>();

        for (String reg : splitedRegions) {

            String url = "https://" + reg + ".api.riotgames.com/lol/summoner/v3/summoners/by-name/" + name;

            ListenableFuture<ResponseEntity<SummonerNameRequest>> listenableFuture = asyncRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity(createHeaders(key)), SummonerNameRequest.class);
            listenableFutures.put(listenableFuture, reg);
        }

        listenableFutures.forEach((key, value) ->
        {
            try {
                if (key.get().getStatusCodeValue() == 200) {
                    regions.add(value);
                }

            } catch (ExecutionException e) {
                if (e.getCause() instanceof HttpClientErrorException) {
                    HttpClientErrorException ex = (HttpClientErrorException) e.getCause();
                    if (ex.getRawStatusCode() != 404) {
                        log.error("there is a problem with region searching", e);
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error with threading", e);
            }

        });

        return regions;
    }

    public SummonerBasicInfoResponse getSummonerData(String name, String region) {

        String url = "https://" + region + ".api.riotgames.com/lol/summoner/v3/summoners/by-name/" + name;

        ResponseEntity<SummonerBasicInfoResponse> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(createHeaders(key)), SummonerBasicInfoResponse.class);
        return response.getBody();
    }

    public MatchesResponse getMatchesData(String region, String id) {

        String url = "https://" + region + ".api.riotgames.com/lol/match/v3/matchlists/by-account/" + id + "?endIndex=" + numberOfMachtes;
        ResponseEntity<MatchesResponse> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(createHeaders(key)), MatchesResponse.class);
        return response.getBody();
    }

    public SingleMatchData getSingleMatchData(String id, String region) {
        String url = "https://" + region + ".api.riotgames.com/lol/match/v3/matches/" + id;
        ResponseEntity<SingleMatchData> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(createHeaders(key)), SingleMatchData.class);
        return response.getBody();
    }

    public List<SummonerLeagueResponse> getSummonerLeague(String id, String region) {
        String url = "https://" + region + ".api.riotgames.com/lol/league/v3/positions/by-summoner/" + id;
        ResponseEntity<List<SummonerLeagueResponse>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(createHeaders(key)), new ParameterizedTypeReference<List<SummonerLeagueResponse>>() {
        });
        return response.getBody();

    }

    public MatchResponse getMatchData(String id, String region) {
        String url = "https://" + region + ".api.riotgames.com/lol/match/v3/matches/" + id;
        ResponseEntity<MatchResponse> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(createHeaders(key)), MatchResponse.class);
        return response.getBody();
    }

}