package computerdatabase;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class IsolateProcess extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://computer-database.gatling.io")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            .acceptEncodingHeader("gzip, deflate, br")
            .acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8,de;q=0.7")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Mobile Safari/537.36");

// seperate request modules


    private ChainBuilder searchComp = exec(
                    http("SearchComputer")
                            .get("/computers?f=arra"),
                    pause(5),
                    http("SelectComputer")
                            .get("/computers/355"),
                    pause(5)
            );

    private ChainBuilder createComp = exec(
                    http("CreateComputer")
                            .post("/computers")
                            .formParam("name", "MyComputer")
                            .formParam("introduced", "2022-08-06")
                            .formParam("discontinued", "2024-08-06")
                            .formParam("company", "2"),
                    pause(5)

            );

    private ChainBuilder deleteComp = exec(
                    http("DeleteComputer")
                            .post("/computers/355/delete")
            );

// all requests are grouped together
//    private ScenarioBuilder scn = scenario("DemoTest3")
//            .exec(
//                    http("LoadHomePage")
//                            .get("/computers"),
//                    pause(5),
//                    http("NewComputerPage")
//                            .get("/computers/new"),
//                    pause(5),
//                    http("CreateComputer")
//                            .post("/computers")
//                            .formParam("name", "MyComputer")
//                            .formParam("introduced", "2022-08-06")
//                            .formParam("discontinued", "2024-08-06")
//                            .formParam("company", "2"),
//                    pause(5),
//                    http("SearchComputer")
//                            .get("/computers?f=arra"),
//                    pause(5),
//                    http("SelectComputer")
//                            .get("/computers/355"),
//                    pause(5),
//                    http("DeleteComputer")
//                            .post("/computers/355/delete")
//            );

    private ScenarioBuilder users = scenario("MyScenario").exec(searchComp);
    private ScenarioBuilder admin = scenario("MyScenario1").exec(createComp,deleteComp);

//    {
//        setUp(
//                users.injectOpen(rampUsers(10).during(10)),
//                admin.injectOpen(rampUsers(2).during(10))
//        ).protocols(httpProtocol);
//    }

    {
        setUp(
                users.injectOpen(
                        nothingFor(4), // 1
                        atOnceUsers(10), // 2
                        rampUsers(10).during(5), // 3
                        constantUsersPerSec(20).during(15), // 4
                        constantUsersPerSec(20).during(15).randomized(), // 5
                        rampUsersPerSec(10).to(20).during(10), // 6
                        rampUsersPerSec(10).to(20).during(10).randomized(), // 7
                        stressPeakUsers(20).during(20) // 8
                ),
                admin.injectOpen(
                                        nothingFor(4), // 1
                                        atOnceUsers(10), // 2
                                        rampUsers(10).during(5), // 3
                                        constantUsersPerSec(20).during(15), // 4
                                        constantUsersPerSec(20).during(15).randomized(), // 5
                                        rampUsersPerSec(10).to(20).during(10), // 6
                                        rampUsersPerSec(10).to(20).during(10).randomized(), // 7
                                        stressPeakUsers(30).during(20) // 8
                                )
                        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(100),
                        global().successfulRequests().percent().gt(95.00),
                        global().responseTime().percentile(99).lt(200),
                        forAll().responseTime().min().lt(500)
                );

    }

}
