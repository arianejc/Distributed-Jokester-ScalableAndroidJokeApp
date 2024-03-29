// Ariane Correa
// ajcorrea

package helloandroid_servlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class JokeModel {
    Gson gson = new Gson();
    public static final String NINJA_API_STRING = "https://v2.jokeapi.dev/joke/";

    /**
     * To fetch the calories from the external api
     *
     * @param category
     * @return
     */

    public static JsonObject getJoke(String category) {
        String jokeUrl = NINJA_API_STRING + category;
        System.out.println("jokeUrl::"+jokeUrl);
        URL u = null;
        try {
            u = new URL(jokeUrl);
            final URLConnection conn = u.openConnection();

            conn.connect();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            System.out.println("jsonObject::"+jsonObject);

            return jsonObject;
        } catch (MalformedURLException e) {
            System.out.println("Incorrect URL");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsuppored encoding exception");
        } catch (IOException e) {
            System.out.println("Input/Output format incorrect");
        }
        return null;
    }


    /**
     * Method to write values to mongo database
     *
     * @param phoneModel
     * @param requestParams
     * @param startTime
     * @param endTime
     * @param apiRequest
     * @param apiResponse
     */
    public static void writeToDatabase(String phoneModel, String requestParams, String startTime, String endTime, String apiRequest, String apiResponse) {
        ConnectionString connectionString = new ConnectionString("mongodb://ajcorrea:correa@ac-ecbysby-shard-00-02.ubkzsko.mongodb.net:27017,ac-ecbysby-shard-00-01.ubkzsko.mongodb.net:27017,ac-ecbysby-shard-00-00.ubkzsko.mongodb.net:27017/myFirstDatabase?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
        // Client settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
        Document document = new Document();
        // creating document with all logged values
        document.append("phone_model", phoneModel);
        document.append("request_param", requestParams);
        document.append("startTime", startTime);
        document.append("endTime", endTime);
        document.append("apiRequest", apiRequest);
        document.append("apiResponse", apiResponse);
        //Inserting the document into the collection
        database.getCollection("myCollection").insertOne(document);

        System.out.println("phone_model: " + document.getString("phone_model"));
        System.out.println("request_param: " + document.getString("request_param"));
        System.out.println("startTime: " + document.getString("startTime"));
        System.out.println("endTime: " + document.getString("endTime"));
        System.out.println("apiRequest: " + document.getString("apiRequest"));
        System.out.println("apiResponse: " + document.getString("apiResponse"));

        System.out.println("Record inserted successfully");

    }

}
