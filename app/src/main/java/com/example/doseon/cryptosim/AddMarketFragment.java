package com.example.doseon.cryptosim;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import util.CustomExpandableListAdapter;
import util.Market;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMarketFragment extends Fragment implements View.OnClickListener {

    private Spinner market_spinner;

    private ArrayList<String> available_market_list;

    private HashMap<String, Market> available_market_map;

    private Market current_market;

    private OnFragmentInteractionListener mListener;

    public AddMarketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_market, container, false);

        market_spinner = (Spinner) v.findViewById(R.id.available_markets);

        market_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_market = available_market_map.get(available_market_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                market_spinner.setSelection(0);
            }
        });

        Button add_button = (Button) v.findViewById(R.id.add_market_button);
        add_button.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //potential argument passes in the future.
            available_market_list = getArguments().getStringArrayList(getString(R.string.AVAILABLE_MARKET_LIST));
            available_market_map = (HashMap<String, Market>)
                    getArguments().getSerializable(getString(R.string.AVAILABLE_MARKET_MAP));
            current_market = available_market_map.get(available_market_list.get(0));

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, available_market_list);

            market_spinner.setAdapter(adapter);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Handles clicking on change password button.
     * @param v View.
     */
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch(v.getId()) {
                case R.id.add_market_button:
                    mListener.add_market(current_market);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void add_market(Market market);
    }



}
