package handler;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doseon.cryptosim.MarketListActivity;
import com.example.doseon.cryptosim.R;


import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.concurrent.atomic.AtomicInteger;

import util.Market;

import static util.Links.GET_WALLET_LINK;

/**
 * Created by Doseon on 2/25/2018.
 */

public class GetWalletFromDBAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketListActivity activity;

    // Map of the markets to store the market ID for detail search.
    private HashMap<String, BigDecimal> wallet_map;

    private String email;

    private Market market;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public GetWalletFromDBAsync(MarketListActivity activity, HashMap<String,
            BigDecimal> wallet_map, String email, Market market) {
        this.activity = activity;
        this.wallet_map = wallet_map;
        this.email = email;
        this.market = market;
    }

    /**
     * Runs in background.
     * Sends request to backend to retrieve all coins from database
     * @param details zip code.
     * @return string response (JSON string) to OnPostExecute.
     */
    @Override
    protected String doInBackground(Void... details) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL urlObject = new URL(GET_WALLET_LINK + "?name=" + email);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
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
        return response;
    }

    /**
     * Converts JSON string to object and populates
     * list of markets from it.
     * @param response JSON string.
     */
    @Override
    protected void onPostExecute(String response) {
        Log.d("API_TEST", response);
        // Something wrong with the network or the URL.
        if (response.startsWith("Unable to")) {
            Toast.makeText(activity.getApplicationContext(), response, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            try {
                JSONObject js_result = new JSONObject(response);
                JSONArray js_array = new JSONArray(js_result.getString("wallet_data"));

                wallet_map.clear();

                for(int i = 0; i < js_array.length(); i++){
                    JSONObject obj = js_array.getJSONObject(i);

                    String coin_name = obj.getString("coin_name");
                    BigDecimal coin_amount = BigDecimal.valueOf(obj.getDouble("coin_amount"));

                    wallet_map.put(coin_name, coin_amount);
                }

                if (market != null) {
                    TextView base_balance = (TextView) activity.findViewById(R.id.base_balance_text);
                    TextView alt_balance = (TextView) activity.findViewById(R.id.alt_balance_text);
                    base_balance.setText("Your " + market.getBaseCoin() + " balance: " + wallet_map.get(market.getBaseCoin()));
                    alt_balance.setText("Your " + market.getAltCoin() + " balance: " + wallet_map.get(market.getAltCoin()));
                }

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