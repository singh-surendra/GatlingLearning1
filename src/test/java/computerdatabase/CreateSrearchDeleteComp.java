package computerdatabase;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class CreateSrearchDeleteComp extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://computer-database.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8,de;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Mobile Safari/537.36");
  


  private ScenarioBuilder scn = scenario("CreateSrearchDeleteComp")
    .exec(
      http("Load Home Page")
        .get("/computers"),
      pause(4),
      http("New Computer Page")
        .get("/computers/new"),
      pause(5),
      http("Create Computer")
        .post("/computers")
        .formParam("name", "MyComp1")
        .formParam("introduced", "2022-08-06")
        .formParam("discontinued", "2024-08-06")
        .formParam("company", "4"),
      pause(5),
      http("Search computer")
        .get("/computers?f=ace")
              .check(regex("computers/\\d+").exists().saveAs("computerId"))
            ,
      pause(1),
      http("Select Computer #{computerId}")
        .get("/#{computerId}"),
      pause(5),
      http("Delete Computer #{computerId}")
        .post("/#{computerId}/delete")
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
