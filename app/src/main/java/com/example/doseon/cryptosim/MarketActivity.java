package com.example.doseon.cryptosim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import handler.AddMarketPostAsync;
import handler.GetMarketListAPIAsync;
import handler.GetMarketsFromDBAsync;
import handler.GetTransactionFromDBAsync;
import handler.GetWalletFromDBAsync;
import handler.RemoveMarketPostAsync;
import handler.StartTransactionPostAsync;
import util.Market;
import util.Posts;

import static android.R.attr.type;
import static util.Links.ADD_MARKET_URL;
import static util.Links.REMOVE_MARKET_URL;
import static util.Links.TRANSACTION_LINK;

public class MarketActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DetailFragment.OnFragmentInteractionListener, AddMarketFragment.OnFragmentInteractionListener,
        RemoveMarketFragment.OnFragmentInteractionListener {


    private ArrayList<String> market_list;
    private HashMap<String, Market> market_map;
    private HashMap<String, BigDecimal> wallet_map;
    private ArrayList<String> wallet_list;
    private String email;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);

        market_list = new ArrayList<String>();
        market_map = new HashMap<String, Market>();
        wallet_map = new HashMap<String, BigDecimal>();
        wallet_list = new ArrayList<String>();
        this.email = mPrefs.getString(getString(R.string.SAVEDNAME), "");
        int admin = mPrefs.getInt(getString(R.string.SAVEDADMIN), 0);
        if (admin == 1) {
            navigationView.getMenu().setGroupVisible(R.id.nav_admin_group, true);
        }

        //navigationView.getMenu().setGroupVisible(R.id.group_1, true)

        GetWalletFromDBAsync updateWalletTask = new GetWalletFromDBAsync(this, wallet_map, wallet_list,
                this.email, null, false);
        updateWalletTask.execute();

        CoinListFragment clf = new CoinListFragment();
        /*Bundle args = new Bundle();
        args.putStringArrayList(getString(R.string.MARKET_LIST), market_list);
        args.putStringArrayList(getString(R.string.WALLET_LIST), wallet_list);
        args.putSerializable(getString(R.string.MARKET_MAP), market_map);
        args.putSerializable(getString(R.string.WALLET_MAP), wallet_map);
        clf.setArguments(args);*/
        updateMarkets(clf, true, true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.market, menu);

        TextView email_text = (TextView) findViewById(R.id.user_email_text);
        email_text.setText(this.email);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_portfolio) {
            GetWalletFromDBAsync updateWalletTask = new GetWalletFromDBAsync(this, wallet_map, wallet_list,
                    this.email, null, true);
            updateWalletTask.execute();
        } else if (id == R.id.nav_transaction) {
            GetTransactionFromDBAsync updateTransactionTask = new GetTransactionFromDBAsync(this, this.email);
            updateTransactionTask.execute();
        } else if (id == R.id.logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            String username = mPrefs.getString(getString(R.string.SAVEDNAME), "");
            String password = mPrefs.getString(getString(R.string.SAVEDPASS), "");
            saveToSharedPrefs(username, password, 0, 0);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_market) {
            CoinListFragment clf = new CoinListFragment();
            updateMarkets(clf, false, true);
        } else if (id == R.id.nav_add_market) {
            GetMarketListAPIAsync getListTask = new GetMarketListAPIAsync(this, market_list, market_map);
            getListTask.execute();
        } else if (id == R.id.nav_remove_market) {
            Bundle args = new Bundle();
            args.putStringArrayList(getString(R.string.MARKET_LIST), market_list);
            args.putSerializable(getString(R.string.MARKET_MAP), market_map);

            RemoveMarketFragment rmf = new RemoveMarketFragment();
            rmf.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, rmf)
                    .addToBackStack(null)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateMarkets(Fragment nextFrag, boolean replace, boolean setup) {

        if (setup) {
            Bundle args = new Bundle();
            args.putStringArrayList(getString(R.string.MARKET_LIST), market_list);
            args.putStringArrayList(getString(R.string.WALLET_LIST), wallet_list);
            args.putSerializable(getString(R.string.MARKET_MAP), market_map);
            args.putSerializable(getString(R.string.WALLET_MAP), wallet_map);
            nextFrag.setArguments(args);
        }
        GetMarketsFromDBAsync updateMarkets= new GetMarketsFromDBAsync(this, market_list, market_map,
                nextFrag, replace);
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
                market, wallet_map, wallet_list, email);
        doTransactionTask.execute();

    }

    /**
     * Saves user credentials to shared preferences.
     * @param name username
     * @param pass user password.
     * @param auto
     */
    public void saveToSharedPrefs(String name, String pass, Integer auto, Integer admin) {
        mPrefs.edit().putString(getString(R.string.SAVEDNAME), name).apply();
        mPrefs.edit().putString(getString(R.string.SAVEDPASS), pass).apply();
        mPrefs.edit().putInt(getString(R.string.SAVEDAUTO), auto).apply();
        mPrefs.edit().putInt(getString(R.string.SAVEDADMIN), admin).apply();
    }

    @Override
    public void add_market(Market market) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", market.getName());
        params.put("base_coin", market.getBaseCoin());
        params.put("alt_coin", market.getAltCoin());

        Posts post = new Posts(params, ADD_MARKET_URL);
        AddMarketPostAsync add_market_task = new AddMarketPostAsync(this, market, wallet_list, wallet_map,
                this.email, post);
        add_market_task.execute();
    }

    @Override
    public void remove_market(Market market) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", market.getName());

        Posts post = new Posts(params, REMOVE_MARKET_URL);
        RemoveMarketPostAsync remove_market_task = new RemoveMarketPostAsync(this, market, post);
        remove_market_task.execute();
    }
}
