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
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

import util.Market;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemoveMarketFragment extends Fragment implements View.OnClickListener {

    private Spinner market_spinner;

    private ArrayList<String> market_list;

    private HashMap<String, Market> market_map;

    private Market current_market;

    private OnFragmentInteractionListener mListener;

    public RemoveMarketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_remove_market, container, false);

        market_spinner = (Spinner) v.findViewById(R.id.current_markets);

        market_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_market = market_map.get(market_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                market_spinner.setSelection(0);
            }
        });

        Button remove_button = (Button) v.findViewById(R.id.remove_market_button);
        remove_button.setOnClickListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //potential argument passes in the future.
            market_list = getArguments().getStringArrayList(getString(R.string.MARKET_LIST));
            market_map = (HashMap<String, Market>)
                    getArguments().getSerializable(getString(R.string.MARKET_MAP));
            current_market = market_map.get(market_list.get(0));

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, market_list);

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
                case R.id.remove_market_button:
                    mListener.remove_market(current_market);
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
        void remove_market(Market market);
    }

}
