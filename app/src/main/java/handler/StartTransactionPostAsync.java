package handler;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doseon.cryptosim.MarketActivity;
import com.example.doseon.cryptosim.MarketListActivity;
import com.example.doseon.cryptosim.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

import util.Market;
import util.Posts;

import static util.Links.UPDATE_PRICES_LINK;
import static util.Posts.getPostDataString;

/**
 * Created by Doseon on 2/25/2018.
 */

public class StartTransactionPostAsync extends AsyncTask<Void, Void, String> {
    /**
     * Market list Activity.
     */
    private MarketActivity activity;

    private Posts postInfo;

    private TextView transaction_message;

    private Button sell_button;

    private Button buy_button;

    private Market market;

    private HashMap<String, BigDecimal> wallet_map;

    private ArrayList<String> wallet_list;

    private String email;

    /**
     * Constructs GetAPIAsync object.
     * Initializes:
     *
     * @param activity SearchActivity
     */
    public StartTransactionPostAsync(MarketActivity activity, Posts post, Market market,
                                     HashMap<String, BigDecimal> wallet_map,
                                     ArrayList<String> wallet_list,
                                     String email) {
        this.activity = activity;
        this.postInfo = post;
        transaction_message = (TextView) activity.findViewById(R.id.transaction_message);
        sell_button = (Button) activity.findViewById(R.id.sell_button);
        buy_button = (Button) activity.findViewById(R.id.buy_button);
        this.market = market;
        this.wallet_map = wallet_map;
        this.wallet_list = wallet_list;
        this.email = email;
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
                String message = mainObject.getString("message");
                if (code == 300) {
                    // SUCCESSFUL TRANSACTION
                    transaction_message.setText(message);

                    GetWalletFromDBAsync updateWalletTask = new GetWalletFromDBAsync(activity, wallet_map, wallet_list,
                            this.email, market, false);
                    updateWalletTask.execute();

                } else if (code == 500) {
                    // NOT ENOUGH $$$
                    transaction_message.setText(message);
                    transaction_message.setError("Insufficient funds.");
                } else {
                    transaction_message.setText(message);
                    transaction_message.setError("Back end error.");
                }
            } catch (Exception ex) {
                //not JSON RETURNED
            }
        }
        buy_button.setEnabled(true);
        sell_button.setEnabled(true);
    }

    /**
     * Pre thread initializations of SearchFragment.
     * Sets button disabled.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        transaction_message.setText("Making transaction... Please wait.");
        transaction_message.setVisibility(View.VISIBLE);
        buy_button.setEnabled(false);
        sell_button.setEnabled(false);
    }
}
