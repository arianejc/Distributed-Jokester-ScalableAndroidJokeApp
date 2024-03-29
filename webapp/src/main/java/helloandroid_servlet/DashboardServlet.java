// Ariane Correa
// ajcorrea

package helloandroid_servlet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
@WebServlet(name = "dashboardServlet", urlPatterns = "/dashboardDetails")
public class DashboardServlet extends HttpServlet {
    DashboardModel dashboardModel;

    public void init() {
        dashboardModel = new DashboardModel();
    }


    /**
     * Get method to display dashboard metrics table
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //fetching model lists
        List<Document> topModelDataList = dashboardModel.fetchTopModels();

        //fetching top params lists
        List<Document> topRequestParamsList = dashboardModel.fetchTopRequestParams();

        // fetching avg latency
        long averageLatency = dashboardModel.findAverageLatency();


        // fetching all value from database
        List<Document> dbDataList = dashboardModel.readFromDatabase();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        System.out.println(dbDataList);


        // writing to html table
        out.println("<html><head><title>Dashboard Metrics</title></head><body>");
        out.println("<h1>Dashboard Metrics</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Start Time</th><th>End Time</th><th>Phone Model</th><th>Request Parameters</th><th>API request</th><th style=\"max-width: 250px\">API response</th></tr>");


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Document dbData : dbDataList) {

            // parsing long time string to readable date format
            Instant startTimeInstant = Instant.ofEpochMilli(Long.parseLong(dbData.getString("startTime")));
            Instant endTimeInstant = Instant.ofEpochMilli(Long.parseLong(dbData.getString("endTime")));

            LocalDateTime localDateTime1 = LocalDateTime.ofInstant(startTimeInstant, ZoneOffset.UTC);
            LocalDateTime localDateTime2 = LocalDateTime.ofInstant(endTimeInstant, ZoneOffset.UTC);

            String startTimeFormatted = localDateTime1.format(formatter);
            String endTimeFormatted = localDateTime2.format(formatter);


            //writing fetched values to table
            out.println("<tr>");
            out.println("<td>" + startTimeFormatted + "</td>");
            out.println("<td>" + endTimeFormatted + "</td>");
            out.println("<td>" + dbData.getString("phone_model") + "</td>");
            out.println("<td>" + dbData.getString("request_param") + "</td>");
            out.println("<td>" + dbData.getString("apiRequest") + "</td>");


            Gson gson = new Gson();
            JsonObject item = gson.fromJson(dbData.getString("apiResponse"), JsonObject.class);

            String joke = null;
            String setup = null;
            String delivery = null;

            // Check if the item has a "joke" field
            if (item.has("joke")) {
                joke = item.get("joke").getAsString();
            } else if (item.has("setup") && item.has("delivery")) {
                // If not, check if it has "setup" and "delivery" fields
                setup = item.get("setup").getAsString();
                delivery = item.get("delivery").getAsString();
            }

            String apiResponse = "- Joke: " + (joke != null ? joke : "null")
                    + "<br>"
                    + "- Setup: " + (setup != null ? setup : "null")
                    + "<br>"
                    + "- Delivery: " + (delivery != null ? delivery : "null");
            out.println("<td>" + apiResponse + "</td>");
            out.println("</tr>");
        }

        out.println("</table>");


        // looping and displaying top model calls
        out.println("<br><h2> Top Model Calls </h2>");
        for (Document topModel : topModelDataList) {
            String phoneModel = topModel.getString("_id");
            int count = topModel.getInteger("count");
            out.println("<h3>" + count + ": " + phoneModel + "</h3>");
        }

        // looping and displaying top request calls
        out.println("<br><h2> Top Request Calls </h2>");
        for (Document topRequest : topRequestParamsList) {
            String phoneModel = topRequest.getString("_id");
            int count = topRequest.getInteger("count");
            out.println("<h3>" + count + ": " + phoneModel + "</h3>");
        }

        // displaying average latency
        out.println("<br><h2> Average Latency : " + averageLatency + " ms </h2>");
        out.println("</body></html>");
    }
}

