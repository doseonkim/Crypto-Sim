package com.example.doseon.cryptosim;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import handler.GetMarketsFromDBAsync;
import util.CustomExpandableListAdapter;
import util.Market;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoinListFragment extends Fragment implements View.OnClickListener,
        android.widget.AdapterView.OnItemClickListener {

    private ExpandableListView lv;
    private ArrayList<String> market_list;
    private HashMap<String, Market> market_map;
    private HashMap<String, BigDecimal> wallet_map;

    ExpandableListAdapter expandableListAdapter;
    HashMap<String, List<String>> expandableListDetail;
    List<String> marketTypes;

    public CoinListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_coin_list, container, false);

        lv = (ExpandableListView) v.findViewById(R.id.expandableListView);

        lv.setOnItemClickListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String market_name = (String) lv.getItemAtPosition(position);
                BigDecimal price = market_map.get(market_name).getPrice();
                Log.d("YOU SELECTED COIN: " + market_name, "Price: " + price);

                /*DetailFragment df = new DetailFragment();
                Bundle args = new Bundle();
                args.putString(getString(R.string.DETAIL_COIN_NAME), coin_name);
                args.putSerializable(getString(R.string.DETAIL_COIN_USD), price_usd);
                args.putSerializable(getString(R.string.DETAIL_COIN_BTC), price_btc);
                df.setArguments(args);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, df)
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //potential argument passes in the future.
            market_list = getArguments().getStringArrayList(getString(R.string.MARKET_LIST));
            market_map = (HashMap<String,Market>)
                    getArguments().getSerializable(getString(R.string.MARKET_MAP));

            wallet_map = (HashMap<String,BigDecimal>)
                    getArguments().getSerializable(getString(R.string.WALLET_MAP));
            createCustomList();

            expandableListAdapter = new CustomExpandableListAdapter(getActivity(), marketTypes,
                    expandableListDetail);

            lv.setAdapter(expandableListAdapter);

            lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    /*Toast.makeText(
                            getActivity().getApplicationContext(),
                            marketTypes.get(groupPosition)
                                    + " -> "
                                    + expandableListDetail.get(
                                    marketTypes.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT
                    ).show();*/

                    DetailFragment df = new DetailFragment();
                    Bundle args = new Bundle();

                    args.putString(getString(R.string.DETAIL_MARKET_NAME), expandableListDetail.get(
                            marketTypes.get(groupPosition)).get(childPosition));
                    args.putSerializable(getString(R.string.DETAIL_MARKET_MAP), market_map);
                    args.putSerializable(getString(R.string.WALLET_MAP), wallet_map);
                    df.setArguments(args);

                    ((MarketActivity)getActivity()).updateMarkets(df, false, false);

                    return true;
                }
            });
        }

    }

    private void createCustomList() {
        marketTypes = new ArrayList<String>();
        marketTypes.add("Bitcoin Market");
        marketTypes.add("Ethereum Market");
        marketTypes.add("USD Market");

        List<String> btc = new ArrayList<String>();
        List<String> eth = new ArrayList<String>();
        List<String> usd = new ArrayList<String>();

        /*for (String mn : market_list) {
            if (mn.contains("btc-")) btc.add(market_map.get(mn).getAltCoin());
            else if (mn.contains("eth-")) eth.add(market_map.get(mn).getAltCoin());
            else usd.add(market_map.get(mn).getAltCoin());
        }*/

        for (String mn : market_list) {
            if (mn.contains("btc-")) btc.add(mn);
            else if (mn.contains("eth-")) eth.add(mn);
            else usd.add(mn);
        }

        expandableListDetail = new HashMap<String, List<String>>();

        expandableListDetail.put("Bitcoin Market", btc);
        expandableListDetail.put("Ethereum Market", eth);
        expandableListDetail.put("USD Market", usd);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {

    }

}
