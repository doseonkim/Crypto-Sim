package handler;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doseon.cryptosim.MarketActivity;
import com.example.doseon.cryptosim.MarketListActivity;
import com.example.doseon.cryptosim.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.Posts;

import static util.Links.UPDATE_PRICES_LINK;
import static util.Posts.getPostDataString;

/**
 * Created by Doseon on 2/25/2018.
 */

public class NoHandlePostAsync extends AsyncTask<Void, Void, String> {
    /**
     * Market list Activity.
     */
    private Activity activity;

    private Posts postInfo;


    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     *
     * @param activity SearchActivity
     */
    public NoHandlePostAsync(Activity activity, Posts post) {
        this.activity = activity;
        this.postInfo = post;
    }

    /**
     * Runs in background.
     * Sends POST request to backend.
     * @param voids no params.
     * @return string response to onPostExecute.
     */
    @Override
    public String doInBackground(Void... voids) {
        String response = "";
        HttpURLConnection urlConnection = null;
        HashMap<String, String> map = postInfo.postSet;
        try {
            URL urlObject = new URL(postInfo.url);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(map));

            writer.flush();
            writer.close();
            os.close();

            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            response = "Unable to connect, Reason: "
                    + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        //Log.d("POST_RESPONse", response);
        return response;
    }

    /**
     * Decides what to do next depending on response.
     * Depending on response can:
     * Successfully register user;
     * Prompt with incorrect pin;
     * Prompt with problem in backend;
     * Prompt with no pin for given email.
     * @param response determines what to do next.
     */
    @Override
    protected void onPostExecute(String response) {
        // Something wrong with the network or the URL.
        if (response.startsWith("Unable to")) {
            Toast.makeText(activity.getApplicationContext(), response, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            try {
                JSONObject mainObject = new JSONObject(response);
                Integer code = mainObject.getInt("code");
                if (code != 300) {
                    Toast.makeText(activity.getApplicationContext(),
                            "Back end error, please try again later.", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex) {
                //not JSON RETURNED
            }
        }
    }

    /**
     * Pre thread initializations of SearchFragment.
     * Sets button disabled.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
