package handler;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doseon.cryptosim.MarketActivity;
import com.example.doseon.cryptosim.MarketListActivity;
import com.example.doseon.cryptosim.PortfolioFragment;
import com.example.doseon.cryptosim.R;
import com.example.doseon.cryptosim.TransactionFragment;


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


import util.Market;

import static com.example.doseon.cryptosim.R.menu.market;
import static util.Links.GET_TRANSACTION_LINK;
import static util.Links.GET_WALLET_LINK;

/**
 * Created by Doseon on 2/25/2018.
 */

public class GetTransactionFromDBAsync extends AsyncTask<Void, Void, String> {
    /**
     * SearchActivity.
     */
    private MarketActivity activity;

    private String email;

    private ArrayList<String> transaction_list;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     * @param activity MarketListActivity
     */
    public GetTransactionFromDBAsync(MarketActivity activity, String email) {
        this.activity = activity;
        this.email = email;
        this.transaction_list = new ArrayList<String>();
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
            URL urlObject = new URL(GET_TRANSACTION_LINK + "?name=" + email);
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
                JSONArray js_array = new JSONArray(js_result.getString("transaction_data"));


                for(int i = 0; i < js_array.length(); i++){
                    JSONObject obj = js_array.getJSONObject(i);

                    String record_data = obj.getString("record");
                    transaction_list.add(record_data);
                }

                TransactionFragment tf = new TransactionFragment();
                Bundle args = new Bundle();
                args.putStringArrayList(activity.getString(R.string.TRANSACTION_LIST), transaction_list);
                tf.setArguments(args);

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, tf)
                        .addToBackStack(null)
                        .commit();

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