package handler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.doseon.cryptosim.CoinListFragment;
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

import util.Posts;

import static android.R.attr.id;
import static util.Links.GET_COINS_LINK;
import static util.Links.PRICE_API_LINK;

/**
 * Created by Doseon on 2/25/2018.
 */

public class GetCoinPricesAPIAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketListActivity activity;

    // Map of the markets to store the market ID for detail search.
    private HashMap<String, BigDecimal> myUSDMap;

    private HashMap<String, BigDecimal> myBTCMap;

    private String coin_name;

    private final AtomicInteger counter;

    private Fragment nextFrag;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public GetCoinPricesAPIAsync(MarketListActivity activity,
                                HashMap<String, BigDecimal> usdmap, HashMap<String, BigDecimal> btcmap,
                                 String coin_name, Fragment nextFrag, AtomicInteger counter) {
        this.activity = activity;
        this.myUSDMap = usdmap;
        this.myBTCMap = btcmap;
        this.coin_name = coin_name;
        this.nextFrag = nextFrag;
        this.counter = counter;
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
            URL urlObject = new URL(PRICE_API_LINK + coin_name);
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
                Log.d("RESPONSE", response);
                JSONArray obj_array = new JSONArray(response);
                JSONObject obj = obj_array.getJSONObject(0);
                BigDecimal usd = new BigDecimal(obj.getString("price_usd"));
                BigDecimal btc = new BigDecimal(obj.getString("price_btc"));

                myUSDMap.put(coin_name, usd);
                myBTCMap.put(coin_name, btc);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("name", coin_name);
                params.put("usd", usd.toString());
                params.put("btc", btc.toString());

                Posts post = new Posts(params);

                UpdatePricePostAsync updateDBPrice = new UpdatePricePostAsync(activity, post);
                updateDBPrice.execute();
                int tasksLeft = this.counter.decrementAndGet();
                // If the count has reached zero, all async tasks have finished.
                if (tasksLeft == 0) {
                    Log.d("counter", "all_tasks_have_finished");


                    /*CoinListFragment clf = new CoinListFragment();
                    Bundle args = new Bundle();
                    args.putStringArrayList(activity.getString(R.string.COIN_LIST), itemList);
                    args.putSerializable(activity.getString(R.string.USD_MAP), myUSDMap);
                    args.putSerializable(activity.getString(R.string.BTC_MAP), myBTCMap);
                    clf.setArguments(args);*/
                    if (nextFrag != null) {
                        activity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.main_container, nextFrag)
                                .commit();
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