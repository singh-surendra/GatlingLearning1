package demoFeeder;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class FeedersDemo extends Simulation {

    //protocol
    HttpProtocolBuilder httpProtocol =
            http.baseUrl("https://computer-database.gatling.io");


    //scenarios
    FeederBuilder.Batchable<String> feeder = csv("data/dataFeeder.csv").circular();
    ScenarioBuilder csvFeeder = scenario("Feeder Demo")
            .repeat(4).on(
                    feed(feeder)
                            .exec(session -> {
                                System.out.println("Name: " + session.getString("name"));
                                System.out.println("Job: " + session.getString("job"));
                                System.out.println("Id: " + session.getString("id"));
                                System.out.println("Pages: " + session.getString("pages"));
                                return session;
                            }
            ).pause(1)
                            .exec(http("/#{pages}")
                            .get("/#{pages}"))
            );

    //setup

    {
        setUp(
                csvFeeder.injectOpen(atOnceUsers(1))

        ).protocols(httpProtocol);
    }


}
