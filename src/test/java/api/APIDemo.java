package api;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class APIDemo extends Simulation {

    //protocol
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://reqres.in/api");

    //scenarios
    ScenarioBuilder getUser = scenario("Get API Demo")
            .exec(
                    http("Get User")
                            .get("/users/2")
                            .check(
                                    status().is(200),
                                    jsonPath("$.data.first_name").is("Janet")
                            )).pause(2);

    ScenarioBuilder createUser = scenario("Post API Demo")
            .exec(
                    http("Create User")
                            .post("/users")
                            .asJson()
                            .body(RawFileBody("data/user.json")).asJson()
//                            .body(StringBody("{\n" +
//                                    "    \"name\": \"Suri\",\n" +
//                                    "    \"job\": \"leader\"\n" +
//                                    "}")).asJson()
                            .check(
                                    status().is(201),
                                    jsonPath("$.name").is("Suri")

                            ));

    ScenarioBuilder updateUser = scenario("PUT API Demo")
            .exec(
                    http("Update User")
                            .put("/users/2")
                            .body(RawFileBody("data/user.json")).asJson()
                            .check(
                                    status().is(200),
                                    jsonPath("$.name").is("Suri")

                            ));

    ScenarioBuilder deleteUser = scenario("Delete API Demo")
            .exec(
                    http("Delete User")
                            .delete("/users/2")
                            .check(
                                    status().is(204)
                            )).pause(2);

    //setup

    {
        setUp(
                getUser.injectOpen(atOnceUsers(10)),
                createUser.injectOpen(atOnceUsers(5)),
                updateUser.injectOpen(atOnceUsers(5)),
                deleteUser.injectOpen(atOnceUsers(6))
        ).protocols(httpProtocol);
    }
}
