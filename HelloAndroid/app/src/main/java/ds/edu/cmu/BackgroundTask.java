// Ariane Correa
// ajcorrea

package ds.edu.cmu;

import android.app.Activity;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundTask {

    private MainActivity activity; // The UI thread
    private String category;

    private String joke;


    public BackgroundTask(MainActivity activity, String category, String joke) {
        this.activity = activity;
        this.category = category;
        this.joke = joke;
    }

    private void startBackground() {
        System.out.println("Category::"+category);
        new Thread(() -> {

            doInBackground();
            // This is magic: activity should be set to MainActivity.this
            //    then this method uses the UI thread
            activity.runOnUiThread(() -> onPostExecute());
        }).start();
    }

    public void execute() {
        // There could be more setup here, which is why
        //    startBackground is not called directly
        startBackground();
    }

    // doInBackground( ) implements whatever you need to do on
    //    the background thread.
    // Implement this method to suit your needs
    private void doInBackground() {
//        String category = params[0];

        JSONObject obj = null;
        try {
            obj = search(category);
            if (obj == null) {
                System.out.println("JSON Object null");
            } else {
                System.out.println("Test obj"+obj.get("joke"));
                if(!obj.getString("joke").isEmpty()) {
                    joke = (String) obj.getString("joke");
                }
                if (!obj.getString("setup").isEmpty() && !obj.getString("delivery").isEmpty()){
                    joke = (String)obj.get("setup") + "\n" + (String)obj.get("delivery");;
                }
                System.out.println("joke"+joke);
            }

        } catch (JSONException e) {
            System.out.println("Error encountered while parsing json response");
            //throw new RuntimeException(e);
        }


    }

    // onPostExecute( ) will run on the UI thread after the background
    //    thread completes.
    // Implement this method to suit your needs
    public void onPostExecute() {
        System.out.println("hello");

            activity.displayJoke(joke);


    }

    /*
     * Search Flickr.com for the searchTerm argument, and return a Bitmap that can be put in an ImageView
     */
    private JSONObject search(String category) {
   //     String jokeURL = "http://10.0.2.2:8080/demo-p4-1.0-SNAPSHOT/joke?category=";
          String jokeURL = "https://fluffy-space-rotary-phone-4vx5ggrvw5jc7jjr-8080.app.github.dev/joke?category=";

        String searchString = jokeURL + category;
        System.out.println("jokeURL::"+searchString);
        JSONObject obj = getJSONObjectFromURL(searchString);
        return obj;


    }

    /*
     * Given a url that will request XML, return a Document with that XML, else null
     */
    public JSONObject getJSONObjectFromURL(String urlString) {

        HttpURLConnection urlConnection = null;

        try {

            //connection code from stack overflow
            URL url = new URL(urlString);
            System.out.println(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-Device-Model", Build.MODEL);
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            System.out.println("device model :: " + urlConnection.getRequestProperty("X-Device-Model"));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            String jsonString = sb.toString();
            System.out.println("JSON: " + jsonString);
            return new JSONObject(jsonString);
        } catch (IOException e) {
            System.out.println("Error occurred while trying to connect to the API ");
            System.out.println("Error::" + e);
            throw new RuntimeException(e);
        } catch (JSONException e) {
            System.out.println("Error while parsing to JSON");
            // throw new RuntimeException(e);
        }
        return null;
    }

}