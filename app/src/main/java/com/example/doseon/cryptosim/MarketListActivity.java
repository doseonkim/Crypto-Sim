package com.example.doseon.cryptosim;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import handler.GetMarketsFromDBAsync;
import handler.GetWalletFromDBAsync;
import handler.StartTransactionPostAsync;
import util.Market;
import util.Posts;

import static util.Links.TRANSACTION_LINK;

public class MarketListActivity extends AppCompatActivity {

    private ArrayList<String> market_list;
    private HashMap<String, Market> market_map;
    private HashMap<String, BigDecimal> wallet_map;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_list);

        if (savedInstanceState == null) {
            if (findViewById(R.id.main_container) != null) {
                market_list = new ArrayList<String>();
                market_map = new HashMap<String, Market>();
                wallet_map = new HashMap<String, BigDecimal>();
                this.email = "doseon@uw.edu";

                /*
                final Handler handler = new Handler();
                Timer timer = new Timer();
                TimerTask doAsynchronousTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    updateMarkets(null);
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                };
                timer.schedule(doAsynchronousTask, 0, 1000 * 60 * 5); //execute in every 5 minutes*/

                /*GetWalletFromDBAsync updateWalletTask = new GetWalletFromDBAsync(this, wallet_map,
                        this.email, null);
                updateWalletTask.execute();

                CoinListFragment clf = new CoinListFragment();
                Bundle args = new Bundle();
                args.putStringArrayList(getString(R.string.MARKET_LIST), market_list);
                args.putSerializable(getString(R.string.MARKET_MAP), market_map);
                args.putSerializable(getString(R.string.WALLET_MAP), wallet_map);
                clf.setArguments(args);
                updateMarkets(clf);*/
            }
        }
    }

    /*public void updateMarkets(Fragment nextFrag) {
        GetMarketsFromDBAsync updateMarkets= new GetMarketsFromDBAsync(this, market_list, market_map,
                nextFrag);
        updateMarkets.execute();
    }

    @Override
    public void create_transaction(String email, String market_name, BigDecimal quantity, String type) {
        String url = TRANSACTION_LINK;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", email);
        params.put("market", market_name);
        params.put("type", type);
        params.put("quantity", quantity.toString());

        Posts post = new Posts(params, url);

        Market market = market_map.get(market_name);

        StartTransactionPostAsync doTransactionTask = new StartTransactionPostAsync(this, post,
                market, wallet_map, email);
        doTransactionTask.execute();

    }*/


/*    private void updateMarketPrice(Fragment nextFrag) {
        GetCoinDatabaseAsync apiTask = new GetCoinDatabaseAsync(this, coinList, coinUSDMap,
                coinBTCMap, nextFrag);
        apiTask.execute();

    }*/

}
