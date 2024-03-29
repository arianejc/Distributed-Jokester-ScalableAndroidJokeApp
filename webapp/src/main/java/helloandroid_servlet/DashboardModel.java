// Ariane Correa
// ajcorrea

package helloandroid_servlet;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DashboardModel {

    Scanner scanner = new Scanner(System.in);

    // Define your MongoDB connection string
    String connectionString = "mongodb://ajcorrea:correa@ac-ecbysby-shard-00-02.ubkzsko.mongodb.net:27017,ac-ecbysby-shard-00-01.ubkzsko.mongodb.net:27017,ac-ecbysby-shard-00-00.ubkzsko.mongodb.net:27017/myFirstDatabase?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";

    ConnectionString cs = new ConnectionString(connectionString);
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(cs)
            .serverApi(ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build())
            .build();
    MongoClient mongoClient = MongoClients.create(settings);

    // Access the database
    MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");

    // Access the collection (or create it if it doesn't exist)
    MongoCollection<Document> collection = database.getCollection("myCollection");

    public List<Document> readFromDatabase() {

        List<Document> values = collection.find().into(new ArrayList<>());

        return values;

    }

    /**
     * Method to fetch top models from the mongodb
     *
     * @return
     */
    public List<Document> fetchTopModels() {


        // reference from http://www.java2s.com/example/java-api/com/mongodb/client/mongocollection/aggregate-1-4.html
        List<Document> list = Arrays.asList(
                //append all values with the same name
                new Document("$group",
                        new Document("_id", "$phone_model")
                                .append("count", new Document("$sum", 1))
                ),

                //sort in descending order
                new Document("$sort", new Document("count", -1)),
                //limit to 5
                new Document("$limit", 5)
        );


        //aggregate and send back to user
        return collection.aggregate(list).into(new ArrayList<>());


    }

    /**
     * Method to fetch top request parameters from database
     *
     * @return
     */
    public List<Document> fetchTopRequestParams() {
        List<Document> pipeline = Arrays.asList(
                //append all values with the same name
                new Document("$group",
                        new Document("_id", "$request_param")
                                .append("count", new Document("$sum", 1))
                ),
                //sort in descending order
                new Document("$sort", new Document("count", -1)),
                //limit to 10
                new Document("$limit", 10)
        );

        return collection.aggregate(pipeline).into(new ArrayList<>());


    }


    /**
     * Method to fetch average latency
     *
     * @return
     */
    public long findAverageLatency() {

        long totalExecutionTime = 0;
        int totalCount = 0;

        // iterating over all values of the table
        MongoCursor<Document> iterator = collection.find().iterator();
        while (iterator.hasNext()) {
            Document doc = iterator.next();
            long startTime = Long.parseLong(doc.getString("startTime"));
            long endTime = Long.parseLong(doc.getString("endTime"));
            long executionTime = endTime - startTime;
            //aagregating total execution time and total count
            totalExecutionTime += executionTime;
            totalCount++;
        }

        //returning the average of execution time
        return totalExecutionTime / totalCount;
    }
}
