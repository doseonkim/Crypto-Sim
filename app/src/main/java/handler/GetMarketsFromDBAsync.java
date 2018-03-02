package handler;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.doseon.cryptosim.MarketActivity;
import com.example.doseon.cryptosim.MarketListActivity;


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


import static util.Links.GET_MARKET_LINK;

/**
 * Created by Doseon on 2/25/2018.
 */

public class GetMarketsFromDBAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketActivity activity;

    /**
     * List of markets.
     */
    private ArrayList<String> market_list;

    // Map of the markets to store the market ID for detail search.
    private HashMap<String, Market> market_map;

    private Fragment nextFrag;

    private boolean replace;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public GetMarketsFromDBAsync(MarketActivity activity, ArrayList<String> market_list,
                                 HashMap<String, Market> market_map,
                                 android.support.v4.app.Fragment nextFrag, Boolean replace) {
        this.activity = activity;
        this.market_list = market_list;
        this.market_map = market_map;
        this.nextFrag = nextFrag;
        this.replace = replace;
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
            URL urlObject = new URL(GET_MARKET_LINK);
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
                JSONArray js_array = new JSONArray(js_result.getString("coins_data"));
                //final ListView list = (ListView) activity.findViewById(R.id.coin_list);
                market_list.clear();
                market_map.clear();

                AtomicInteger counter = new AtomicInteger(js_array.length());
                for(int i = 0; i < js_array.length(); i++){
                    JSONObject obj = js_array.getJSONObject(i);

                    String market_name = obj.getString("market_name");
                    String base_coin = obj.getString("base_coin");
                    String alt_coin = obj.getString("alt_coin");
                    BigDecimal price = BigDecimal.valueOf(obj.getDouble("price"));

                    Market market = new Market(market_name, base_coin, alt_coin, price);

                    market_list.add(market_name);
                    market_map.put(market_name, market);

                    UpdatePricesFromAPIAsync updatePriceTask = new UpdatePricesFromAPIAsync(activity,
                            market_name, market_map, nextFrag, counter, replace);
                    updatePriceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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