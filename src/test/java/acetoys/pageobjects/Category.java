package acetoys.pageobjects;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;


import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
public class Category {

    private static final FeederBuilder<String> categoryFeeder =
            csv("data/categoryDetails.csv").circular();

    public static ChainBuilder productListByCategory =
            feed(categoryFeeder)
                    .exec(
                    http("Load Products List Page - Category: #{categoryName}")
                            .get("/category/#{categorySlug}")
                            .check(css("#CategoryName").isEL("#{categoryName}"))
            );

//once the csv feeder is added, we don't need anymore below lines
//    public static ChainBuilder productListByCategoryBabiesToys =
//            exec(
//                    http("Load Products List Page - Category: Babies Toys")
//                            .get("/category/babies-toys")
//                            .check(css("#CategoryName").is("Babies Toys"))
//            );

    public static ChainBuilder cyclePagesOfProducts =
            exec(session -> {
                int currentPageNumber = session.getInt("productsListPageNumber");
                int totalPages = session.getInt("categoryPages");
                boolean morePages = currentPageNumber < totalPages;
                return session.setAll(Map.of(
                        "currentPageNumber", currentPageNumber,
                        "nextPageNumber", (currentPageNumber + 1),
                        "morePages", morePages));
            })
                    .asLongAs("#{morePages}").on(
                            exec(http("Load page #{currentPageNumber} of Products - Category: #{categoryName}")
                                    .get("/category/#{categorySlug}?page=#{currentPageNumber}")
                                    .check(css(".page-item.active").isEL("#{nextPageNumber}")))
                                    .exec(session -> {
                                        int currentPageNumber = session.getInt("currentPageNumber");
                                        int totalPages = session.getInt("categoryPages");
                                        currentPageNumber++;
                                        boolean morePages = currentPageNumber < totalPages;
                                        return session.setAll(Map.of(
                                                "currentPageNumber", currentPageNumber,
                                                "nextPageNumber", (currentPageNumber + 1),
                                                "morePages", morePages));

                                    })
                    );
}
//
//    public static ChainBuilder loadSecondPageOfProducts =
//            exec(
//                    http("Load Next Page of Products - Page 1")
//                            .get("/category/all?page=1")
//                            .check(css(".page-item.active").is("2"))
//            );
//
//    public static ChainBuilder loadThirdPageOfProducts =
//            exec(
//                    http("Load Next Page of Products - Page 2")
//                            .get("/category/all?page=2")
//                            .check(css(".page-item.active").is("3"))
//            );
//}
