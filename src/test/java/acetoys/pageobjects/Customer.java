package acetoys.pageobjects;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Choice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
public class Customer {

    public static Iterator<Map<String, Object>> loginFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                Random rnd = new Random();
                int userId = rnd.nextInt(3 - 1 + 1) + 1;

                HashMap<String, Object> hmap = new HashMap<>();
                hmap.put("userId", "user" + userId);
                hmap.put("password", "pass");

                return hmap;
            }).iterator();
    public static ChainBuilder login =
            feed(loginFeeder)
                    .exec(
                    http("Login User")
                            .post("/login")
                            .formParam("_csrf", "#{_csrfToken}")
                            .formParam("username", "#{userId}")
                            .formParam("password", "#{password}")
                            .check(css("#_csrf", "content").saveAs("_csrfLoggedToken"))
            )
                    .exec(session -> session.set("customerLoggedIn", true));

    public static ChainBuilder logout =
            randomSwitch().on(
                    Choice.withWeight(10, exec(
                            http("Logout")
                                    .post("/logout")
                                    .formParam("_csrf", "#{_csrfLoggedToken}")
                                    .check(css("#LoginLink").is("Login"))
                    ))
            );



}

