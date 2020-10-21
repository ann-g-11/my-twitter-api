package statuses;

import basic.RestUtilities;
import constants.EndPoints;
import constants.Path;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;

public class TwitterWorkflowTest {
    RequestSpecification reqSpec;
    ResponseSpecification resSpec;
    String tweetId = "";


    @BeforeClass
    public void setup() {
        reqSpec = RestUtilities.getRequestSpecification();
        reqSpec.basePath(Path.STATUSES);

        resSpec = RestUtilities.getResponseSpecification();
    }

    @Test
    public void postTweets() {
        Response response =
                given()
                        .spec(RestUtilities.createQueryParam(reqSpec, "status", "My First Tweet"))
                        .when()
                        .post(EndPoints.STATUSES_TWEET_POST)
                        .then()
                        .log().all()
                        .spec(resSpec)
                        .extract()
                        .response();

        //tweetId = response.path("id_str");

        JsonPath jsonPath=RestUtilities.getJsonPath(response);
        tweetId = jsonPath.get("id_str");
        System.out.println("The response.path: " + tweetId);
    }

    @Test(dependsOnMethods = {"postTweets"})
    public void readTweet() {
        RestUtilities.setEndPoint(EndPoints.STATUSES_TWEET_READ_SINGLE);
        Response res = RestUtilities.getResponse(
                RestUtilities.createQueryParam(reqSpec, "id", tweetId), "get");

        String text = res.path("text");
        System.out.println("Read Tweets Method");
        Assert.assertTrue(text.contains("My First Tweet"));
    }

    @Test(dependsOnMethods = {"readTweet"})
    public void deleteTweet() {
        RestUtilities.setEndPoint(EndPoints.STATUSES_TWEET_DESTROY);
        Response res = RestUtilities.getResponse(
                RestUtilities.createQueryParam(reqSpec, "id", tweetId), "post");

    }
}
