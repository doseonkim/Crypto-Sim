package handler;

/**
 * Created by Doseon on 3/4/2018.
 */


import android.os.AsyncTask;
import android.widget.Toast;

import com.example.doseon.cryptosim.CoinListFragment;
import com.example.doseon.cryptosim.MarketActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import util.Market;
import util.Posts;

import static util.Posts.getPostDataString;

public class RemoveMarketPostAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketActivity activity;

    private Market market;

    private Posts post;


    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public RemoveMarketPostAsync(MarketActivity activity, Market market,
                              Posts post) {
        this.activity = activity;
        this.market = market;
        this.post = post;
    }

    /**
     * Runs in background.
     * Sends request to backend to retrieve all coins from database
     * @return string response (JSON string) to OnPostExecute.
     */
    @Override
    public String doInBackground(Void... params) {
        String response = "";
        HttpURLConnection urlConnection = null;
        String url = post.url;
        HashMap<String, String> map = post.postSet;
        try {
            URL urlObject = new URL(url);
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
     * Converts JSON string to object and populates
     * list of markets from it.
     * @param response JSON string.
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
                String message = "Error occurred, please try again.";
                if (code == 300) {
                    //CORRECT PIN
                    message = market.getName() + " has been removed.";
                } else {
                    message = "Back end error, please try again later.";
                }
                activity.updateMarkets(new CoinListFragment(), false, true);
                Toast.makeText(activity.getApplicationContext(),
                        message, Toast.LENGTH_LONG).show();
                return;
            } catch (Exception ex) {
                //not JSON RETURNED
            }
        }
    }

    //

    /**
     * Prepares thread.
     * Sets search button to disabled.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}