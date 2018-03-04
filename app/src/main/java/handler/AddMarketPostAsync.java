package handler;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doseon.cryptosim.CoinListFragment;
import com.example.doseon.cryptosim.MarketActivity;
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
import java.util.concurrent.atomic.AtomicInteger;

import util.Market;
import util.Posts;

import static util.Links.ADD_MARKET_URL;
import static util.Links.BITTREX_API_LINK;
import static util.Links.UPDATE_PRICES_DB_LINK;
import static util.Posts.getPostDataString;

/**
 * Created by Doseon on 3/3/2018.
 */

public class AddMarketPostAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketActivity activity;

    // Map of the markets to store the market ID for detail search.
    private HashMap<String, BigDecimal> wallet_map;

    private ArrayList<String> wallet_list;

    private Market market;

    private Posts post;

    private String email;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public AddMarketPostAsync(MarketActivity activity, Market market,
                              ArrayList<String> wallet_list, HashMap<String, BigDecimal> wallet_map,
                              String email, Posts post) {
        this.activity = activity;
        this.market = market;
        this.wallet_map = wallet_map;
        this.wallet_list = wallet_list;
        this.email = email;
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
                    GetWalletFromDBAsync updateWalletTask = new GetWalletFromDBAsync(activity,
                            wallet_map, wallet_list,
                            email, null, false);
                    updateWalletTask.execute();
                    message = market.getName() + " has been added.";
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