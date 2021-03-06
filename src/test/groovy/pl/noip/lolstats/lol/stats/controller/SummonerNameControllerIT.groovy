package pl.noip.lolstats.lol.stats.controller

import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.noip.lolstats.lol.stats.jwt.JwtGeneratorImpl
import pl.noip.lolstats.lol.stats.jwt.JwtParserImpl
import pl.noip.lolstats.lol.stats.model.Account
import pl.noip.lolstats.lol.stats.repository.AccountRepository
import pl.noip.lolstats.lol.stats.time.TimeService
import pl.noip.lolstats.lol.stats.time.TimeServiceImpl
import pl.noip.lolstats.lol.stats.utils.Sha
import spock.lang.Specification

import static io.restassured.RestAssured.given

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SummonerNameControllerIT extends Specification {

    JwtGeneratorImpl jwtGenerator

    JwtParserImpl jwtParser

    TimeService timeService = new TimeServiceImpl()

    def account = Account.builder()
            .email("example@mail.com")
            .passwordHash(Sha.hash("examplePassword"))
            .sumName("exampleName")
            .region("exampleRegion")
            .id("1354653")
            .accountId("123151")
            .build()

    @LocalServerPort
    private int webPort

    @Autowired
    private AccountRepository repository

    private static final String PATH = "/api/summoner/name"
    private static final String AUTHORIZATION = "Authorization"

    def setup() {
        repository.deleteAll()
        jwtGenerator = new JwtGeneratorImpl(timeService)
        jwtGenerator.setSecret("dh1asg2fhksdf4jkla9edhgfk8jadsh7flas3dsdf4gbhjkfews5rrtweherhedrtf6gwetygedrgwergsed2rgwergwrfgwefwe")
        jwtParser = new JwtParserImpl(timeService)
        jwtParser.setSecret("dh1asg2fhksdf4jkla9edhgfk8jadsh7flas3dsdf4gbhjkfews5rrtweherhedrtf6gwetygedrgwergsed2rgwergwrfgwefwe")
    }

    def "Create token with correct data"() {
        given: "jwt is generated as second 1 and checked at second 2"
        def mail = "example@mail.com"
        def sumName = "KubaKonst"
        def region = "eun1"
        repository.save(account)
        def bearerToken = "bearer " + jwtGenerator.generate(account)
        def token = given()
                .port(webPort)
                .when()
                .contentType(ContentType.JSON)
                .header(AUTHORIZATION, bearerToken)
                .body([sumName: sumName, region: region])
                .post(PATH)
                .then()
                .statusCode(200)
                .body("accessToken", Matchers.containsString("."))
                .body("bearer", Matchers.containsString("bearer ")).extract().body().<String> path("accessToken").toString()

        expect:
        jwtParser.jwtInfo(token).email == mail
        jwtParser.jwtInfo(token).name == sumName
        jwtParser.jwtInfo(token).region == region
    }

}
