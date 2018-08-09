package pl.noip.lolstats.lol.stats.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.noip.lolstats.lol.stats.jwt.JwtChecker;
import pl.noip.lolstats.lol.stats.jwt.TokenSplit;

@Configuration
public class Interceptor extends WebMvcConfigurerAdapter {

    private JwtChecker jwtChecker;
    private TokenSplit tokenSplit;

    public Interceptor(JwtChecker jwtChecker, TokenSplit tokenSplit) {
        this.jwtChecker = jwtChecker;
        this.tokenSplit = tokenSplit;

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthFilter(jwtChecker, tokenSplit))
                .addPathPatterns("/api/summoner/name")
                .addPathPatterns("/api/auth/checkToken")
                .addPathPatterns("/api/summoner/basicInfo")
                .addPathPatterns("/api/summoner/matches")
                .addPathPatterns("/api/summoner/league");
    }
}