// Ariane Correa
// ajcorrea

package helloandroid_servlet;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/joke")
public class JokeServlet extends HttpServlet {
    private JokeModel jokeModel;

    public void init() {
        jokeModel = new JokeModel();
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String startTime = String.valueOf(System.currentTimeMillis());
            response.setContentType("text/html");

            // to handle spaces
            String item = request.getParameter("category").replace(" ", "%20");

            String userAgent = request.getHeader("user-agent");
            String phoneModel = null;

            // regex matching to fetch the phone model
            Pattern pattern = Pattern.compile(";\\s*(\\S+)\\s+Build/");  // Matches the device model in User-Agent
            Matcher matcher = pattern.matcher(userAgent);

            if (matcher.find()) {
                phoneModel = matcher.group(1);
            }

            System.out.println("phoneModel::" + phoneModel);

            JsonObject jsonObject = jokeModel.getJoke(item);
            String joke="";
            String delivery = "";
            String setup = "";

            if(jsonObject!=null) {
                if (jsonObject.has("joke")) {
                    joke = jsonObject.get("joke").getAsString();
                } else if (jsonObject.has("setup") && jsonObject.has("delivery")) {
                    // If not, check if it has "setup" and "delivery" fields
                    setup = jsonObject.get("setup").getAsString();
                    delivery = jsonObject.get("delivery").getAsString();
                }
                // creating new json to send back to caller
                JsonObject jokeObj = new JsonObject();
                jokeObj.addProperty("joke", joke);
                jokeObj.addProperty("setup", setup);
                jokeObj.addProperty("delivery", delivery);
                PrintWriter out = null;
                out = response.getWriter();
                out.append(jokeObj.toString());
                String endTime = String.valueOf(System.currentTimeMillis());
                // writing logging parameters to database
                jokeModel.writeToDatabase(phoneModel, request.getParameter("category"), startTime, endTime,
                        jokeModel.NINJA_API_STRING + item, String.valueOf(jsonObject));
            } else {
                //sending empty object if response is null
                PrintWriter out = null;
                out = response.getWriter();
                out.append(new JsonObject().toString());
            }
        }catch (IOException e) {
            System.out.println("Error encountered");
        }

    }

    public void destroy() {
    }
}
