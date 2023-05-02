package acetoys;


import acetoys.pageobjects.Cart;
import acetoys.pageobjects.Category;
import acetoys.pageobjects.Customer;
import acetoys.pageobjects.Product;
import acetoys.pageobjects.StaticPages;
import acetoys.session.UserSession;
import acetoys.simulation.TestPopulation;
import acetoys.simulation.TestScenario;
import acetoys.simulation.UserJourney;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;


import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class AceToysSimulation extends Simulation {

    private static final String TEST_TYPE = System.getProperty("TEST_TYPE", "INSTANT_USERS");

    private static final String DOMAIN = "acetoys.uk";

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://" + DOMAIN)
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*detectportal\\.firefox\\.com.*"))
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-GB,en;q=0.9");


    {

        switch (TEST_TYPE) {
            case "INSTANT_USERS" -> {
                setUp(TestPopulation.complexInjection)
                        .protocols(httpProtocol)
                        .assertions(
                                global().responseTime().mean().lt(3),
                                global().successfulRequests().percent().gt(99.0),
                                forAll().responseTime().max().lt(5)

                        );
            }
            case "RAMP_USERS" -> {
                setUp(TestPopulation.rampUsers).protocols(httpProtocol);

            }
            case "COMPLEX_INJECTION" -> {
                setUp(TestPopulation.complexInjection).protocols(httpProtocol);
            }
            case "CLOSED_MODEL" -> {
                setUp(TestPopulation.closeModel).protocols(httpProtocol);

            }
            default -> {
                setUp(TestPopulation.instantUsers)
                        .protocols(httpProtocol)
                        .assertions(
                                global().responseTime().mean().lt(3),
                                global().successfulRequests().percent().gt(99.0),
                                forAll().responseTime().max().lt(5)

                        );

            }
        }
    }


//    {
//        setUp(TestScenario.highPurchaseLoadTest
//                .injectOpen(atOnceUsers(10)))
//                .protocols(httpProtocol);
//    }


//    private ScenarioBuilder scn = scenario("AceToysSimulation")
//            .exec(UserJourney.browseStore);

//we can comment below line, because we have refactor our stes and move them in UserJourney class.
//    private ScenarioBuilder scn = scenario("AceToysSimulation")
//            .exec(UserSession.initSession)
//            .exec(StaticPages.homePage)
//            .pause(2)
//            .exec(StaticPages.ourStoryPage)
//            .pause(2)
//            .exec(StaticPages.getInTouchPage)
//            .pause(2)
//            .exec(Category.productListByCategory)
//            .pause(2)
//            .exec(Category.cyclePagesOfProducts)
//            .pause(2)
////we are adding cyclePagesOfProducts logic, and dont need below 2 lines
////            .exec(Category.loadSecondPageOfProducts)
////            .pause(2)
////            .exec(Category.loadThirdPageOfProducts)
////            .pause(2)
//            .exec(Product.loadProductDetailsPage)
//            .pause(2)
//            .exec(Product.addProductToCart)
//            .pause(2)
//            .exec(Category.productListByCategory)
//            .pause(2)
//            .exec(Product.addProductToCart)
//            .pause(2)
//            .exec(Product.addProductToCart)
//            .pause(2)
//            .exec(Cart.viewCart)
//            .pause(2)
//            .exec(Cart.increaseQuantityInCart)
//            .pause(2)
//            .exec(Cart.increaseQuantityInCart)
//            .pause(2)
//            .exec(Cart.decreaseQuantityInCart)
//            .pause(2)
//            .exec(Cart.viewCart)
//            .pause(2)
//            .exec(Customer.logout);

/*    {
        setUp(scn.injectOpen(
                atOnceUsers(1))
        ).protocols(httpProtocol);
    }*/

}