// Ariane Correa
// ajcorrea

package ds.edu.cmu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Build;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    MainActivity ma = this;
    TextView jokeTextView = null;
    private EditText categoryEditText;

    private ImageView jokeImageView;

    private String category;
    String joke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jokeImageView = (ImageView) findViewById(R.id.jokeImageView);
        jokeTextView = (TextView) findViewById(R.id.jokeTextView);
        jokeTextView.setText("Hello");
        categoryEditText = (EditText) findViewById(R.id.categoryEditText);


        // Load the image into the ImageView
        jokeImageView.setImageResource(R.drawable.img);

        Button getJokeButton = (Button) findViewById(R.id.getJokeButton);
        getJokeButton.setOnClickListener(v -> {
            //Removed category from here
            System.out.println("Test" + categoryEditText.getText().toString());

            category = ((EditText)findViewById(R.id.categoryEditText)).getText().toString();
            //System.out.println("Test" + category);

            BackgroundTask bt = new BackgroundTask(ma, category,"");
            bt.execute();

        });
    }


/*
 Code referenced from Lab 8
 * This class provides capabilities to search for calories given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of inner class BackgroundTask that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.
 *
 * Method BackgroundTask.doInBackground( ) does the background work
 * Method BackgroundTask.onPostExecute( ) is called when the background work is
 *    done; it calls *back* to ip to report the results
 *
 */

// class BackgroundTask
// Implements a background thread for a long running task that should not be
//    performed on the UI thread. It creates a new Thread object, then calls doInBackground() to
//    actually do the work. When done, it calls onPostExecute(), which runs
//    on the UI thread to update some UI widget (***never*** update a UI
//    widget from some other thread!)
//
// Adapted from one of the answers in
// https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
// Modified by Barrett
//
// Ideally, this class would be abstract and parameterized.
// The class would be something like:
//      private abstract class BackgroundTask<InValue, OutValue>
// with two generic placeholders for the actual input value and output value.
// It would be instantiated for this program as
//      private class MyBackgroundTask extends BackgroundTask<String, Bitmap>
// where the parameters are the String url and the Bitmap image.
//    (Some other changes would be needed, so I kept it simple.)
//    The first parameter is what the BackgroundTask looks up on Flickr and the latter
//    is the image returned to the UI thread.
// In addition, the methods doInBackground() and onPostExecute( ) could be
//    abstract methods; would need to finesse the input and ouptut values.
// The call to activity.runOnUiThread( ) is an Android Activity method that
//    somehow "knows" to use the UI thread, even if it appears to create a
//    new Runnable.

    public void displayJoke(String joke) {
        if (joke != null) {
            System.out.println("joke found");
            jokeTextView.setText(joke);
        } else {
            System.out.println("Nothing found");
            jokeTextView.setText("N/A");
        }


    }

}

