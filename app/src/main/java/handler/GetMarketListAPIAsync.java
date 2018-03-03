package handler;

/**
 * Created by Doseon on 3/3/2018.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.example.doseon.cryptosim.AddMarketFragment;
import com.example.doseon.cryptosim.MarketActivity;
import com.example.doseon.cryptosim.R;

import org.json.JSONArray;
import org.json.JSONObject;

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

import static com.example.doseon.cryptosim.R.menu.market;
import static util.Links.GET_MARKET_LINK;
import static util.Links.GET_MARKET_LIST;

public class GetMarketListAPIAsync extends AsyncTask<Void, Void, String> {
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

    private ArrayList<String> available_market_list;

    // Map of the markets to store the market ID for detail search.
    private HashMap<String, Market> available_market_map;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public GetMarketListAPIAsync(MarketActivity activity, ArrayList<String> market_list,
                                 HashMap<String, Market> market_map) {
        this.activity = activity;
        this.market_list = market_list;
        this.market_map = market_map;
        this.available_market_list = new ArrayList<String>();
        this.available_market_map = new HashMap<String, Market>();
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
            URL urlObject = new URL(GET_MARKET_LIST);
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
                JSONArray js_array = new JSONArray(js_result.getString("result"));

                for(int i = 0; i < js_array.length(); i++){
                    JSONObject obj = js_array.getJSONObject(i);

                    String market_name = obj.getString("MarketName");
                    String base_coin = obj.getString("BaseCurrencyLong");
                    String alt_coin = obj.getString("MarketCurrencyLong");

                    if (!market_list.contains(market_name)) {
                        Market market = new Market(market_name, base_coin, alt_coin, new BigDecimal(1));
                        available_market_list.add(market_name);
                        available_market_map.put(market_name, market);
                    }

                    Bundle args = new Bundle();
                    args.putStringArrayList(activity.getString(R.string.AVAILABLE_MARKET_LIST), available_market_list);
                    args.putSerializable(activity.getString(R.string.AVAILABLE_MARKET_MAP), available_market_map);

                    AddMarketFragment amf = new AddMarketFragment();
                    amf.setArguments(args);

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, amf)
                            .addToBackStack(null)
                            .commit();

                    /*UpdatePricesFromAPIAsync updatePriceTask = new UpdatePricesFromAPIAsync(activity,
                            market_name, market_map, nextFrag, counter, replace);
                    updatePriceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/

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