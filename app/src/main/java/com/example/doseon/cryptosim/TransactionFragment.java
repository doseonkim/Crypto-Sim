package com.example.doseon.cryptosim;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {

    private ArrayList<String> transaction_list;
    private ListView lv;

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        transaction_list = new ArrayList<String>();

        if (getArguments() != null) {
            transaction_list = getArguments().getStringArrayList(getString(R.string.TRANSACTION_LIST));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.list_view_layout, R.id.custom_text_view, transaction_list);

            lv.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_transaction, container, false);

        lv = (ListView) v.findViewById(R.id.transaction_list_view);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.back_to_market_transaction);
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
