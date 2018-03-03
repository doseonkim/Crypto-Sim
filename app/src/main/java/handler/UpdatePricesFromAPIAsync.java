package handler;

/**
 * Created by Doseon on 2/26/2018.
 */

import android.support.v4.app.Fragment;

import java.math.BigDecimal;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doseon.cryptosim.MarketActivity;
import com.example.doseon.cryptosim.MarketListActivity;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import util.Market;
import util.Posts;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static util.Links.BITTREX_API_LINK;
import static util.Links.GET_COINS_LINK;
import static util.Links.PRICE_API_LINK;
import static util.Links.UPDATE_PRICES_DB_LINK;

/**
 * Created by Doseon on 2/25/2018.
 */

public class UpdatePricesFromAPIAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketActivity activity;

    // Map of the markets to store the market ID for detail search.
    private HashMap<String, Market> market_map;

    private String market_name;

    private final AtomicInteger counter;

    private Fragment nextFrag;

    private boolean replace;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public UpdatePricesFromAPIAsync(MarketActivity activity, String market_name,
                                    HashMap<String, Market> market_map,
                                    Fragment nextFrag, AtomicInteger counter, boolean replace) {
        this.activity = activity;
        this.market_map = market_map;
        this.market_name = market_name;
        this.nextFrag = nextFrag;
        this.counter = counter;
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
            URL urlObject = new URL(BITTREX_API_LINK + market_name);
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
        // Something wrong with the network or the URL.
        if (response.startsWith("Unable to")) {
            Toast.makeText(activity.getApplicationContext(), response, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            try {
                //Log.d("RESPONSE", response);
                JSONObject obj = new JSONObject(response);
                JSONArray result = obj.getJSONArray("result");
                JSONObject market = result.getJSONObject(0);

                BigDecimal price = new BigDecimal(market.getString("Last"));

                Log.d("UpdatePrice Async: ", market_name + " price: " + price);

                market_map.get(market_name).setPrice(price);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("name", market_name);
                params.put("price", price.toString());

                Posts post = new Posts(params);
                post.url = UPDATE_PRICES_DB_LINK;

                NoHandlePostAsync updateDBPrice = new NoHandlePostAsync(activity, post);
                updateDBPrice.execute();

                int tasksLeft = this.counter.decrementAndGet();
                //If the count has reached zero, all async tasks have finished.
               if (tasksLeft == 0) {
                   Log.d("counter", "all_tasks_have_finished");


                   /* CoinListFragment clf = new CoinListFragment();
                    Bundle args = new Bundle();
                    args.putStringArrayList(activity.getString(R.string.COIN_LIST), itemList);
                    args.putSerializable(activity.getString(R.string.USD_MAP), myUSDMap);
                    args.putSerializable(activity.getString(R.string.BTC_MAP), myBTCMap);
                    clf.setArguments(args);*/
                    if (nextFrag != null) {
                        if (replace) {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_container, nextFrag)
                                    .commit();
                        } else {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_container, nextFrag)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
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