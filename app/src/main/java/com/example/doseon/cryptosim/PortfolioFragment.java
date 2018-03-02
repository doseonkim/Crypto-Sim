package com.example.doseon.cryptosim;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PortfolioFragment extends Fragment {

    private ArrayList<String> wallet_list;
    private HashMap<String, BigDecimal> wallet_map;
    private ListView lv;




    public PortfolioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        wallet_list = new ArrayList<String>();
        wallet_map = new HashMap<String, BigDecimal>();

        if (getArguments() != null) {
            wallet_list = getArguments().getStringArrayList(getString(R.string.WALLET_LIST));
            wallet_map = (HashMap<String,BigDecimal>)
                    getArguments().getSerializable(getString(R.string.WALLET_MAP));

            ArrayList<String> wallet_details = new ArrayList<String>();

            for (String s : wallet_list) {
                wallet_details.add("[" + s + "] " + wallet_map.get(s));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.list_view_layout, R.id.custom_text_view, wallet_details);

            lv.setAdapter(adapter);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_portfolio, container, false);

        lv = (ListView) v.findViewById(R.id.portfolio_listview);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.back_to_market);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoinListFragment clf = new CoinListFragment();
                ((MarketActivity) getActivity()).updateMarkets(clf, false, true);
            }
        });
        return v;
    }

}
