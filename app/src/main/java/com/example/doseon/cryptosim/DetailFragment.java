package com.example.doseon.cryptosim;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.HashMap;

import util.Market;

import static com.example.doseon.cryptosim.R.id.buy_calc;
import static com.example.doseon.cryptosim.R.id.buy_text;
import static com.example.doseon.cryptosim.R.id.sell_text;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    private String market_name;
    private HashMap<String, Market> market_map;
    private HashMap<String, BigDecimal> wallet_map;

    private Market market;
    private TextView sell_calc;
    private TextView header;
    private TextView current_price;
    private TextView buy_text;
    private TextView sell_text;
    private EditText sell_box;
    private TextView buy_calc;
    private EditText buy_box;
    private Button buy_button;
    private Button sell_button;
    private TextView base_balance;
    private TextView alt_balance;
    private OnFragmentInteractionListener mListener;

    private BigDecimal sell_amount;
    private BigDecimal buy_amount;

    private String email;

    private SharedPreferences mPrefs;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        header = (TextView) v.findViewById(R.id.detail_market_text);

        sell_calc = (TextView) v.findViewById(R.id.sell_calc);

        current_price = (TextView) v.findViewById(R.id.current_price_text);

        buy_text = (TextView) v.findViewById(R.id.buy_text);

        sell_text = (TextView) v.findViewById(R.id.sell_text);

        sell_box = (EditText) v.findViewById(R.id.sell_textbox);

        buy_calc = (TextView) v.findViewById(R.id.buy_calc);
        buy_box = (EditText) v.findViewById(R.id.buy_textbox);

        buy_button = (Button) v.findViewById(R.id.buy_button);
        buy_button.setText("Buy");
        buy_button.setOnClickListener(this);
        sell_button = (Button) v.findViewById(R.id.sell_button);
        sell_button.setOnClickListener(this);
        sell_button.setText("Sell");

        base_balance = (TextView) v.findViewById(R.id.base_balance_text);
        alt_balance = (TextView) v.findViewById(R.id.alt_balance_text);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            market_name = getArguments().getString(getString(R.string.DETAIL_MARKET_NAME));
            market_map = (HashMap<String, Market>)
                    getArguments().getSerializable(getString(R.string.DETAIL_MARKET_MAP));
            wallet_map = (HashMap<String, BigDecimal>)
                    getArguments().getSerializable(getString(R.string.WALLET_MAP));
            market = market_map.get(market_name);
            mPrefs = getActivity().getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);

            this.email = mPrefs.getString(getString(R.string.SAVEDNAME), "");

            header.setText(market.getName().toUpperCase().replace('-', '/') + " Market");

            current_price.setText("Current Price: " + market.getPrice() + " " + market.getBaseCoin() +
            " per 1 " + market.getAltCoin() + ".");

            buy_text.setText("Buy " + market.getAltCoin());
            sell_text.setText("Sell " + market.getAltCoin());
            base_balance.setText("Your " + market.getBaseCoin() + " balance: " + wallet_map.get(market.getBaseCoin()));
            alt_balance.setText("Your " + market.getAltCoin() + " balance: " + wallet_map.get(market.getAltCoin()));



            sell_box.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() >= 1) {
                        try {
                            sell_amount = new BigDecimal(s.toString());
                            BigDecimal amount = market.getPrice().multiply(sell_amount);
                            sell_calc.setText("Total: " + amount.toString() + " " + market.getBaseCoin() + ".");
                        } catch (Exception ex) {
                            // when words are typed.
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            buy_box.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() >= 1) {
                        try {
                            buy_amount = new BigDecimal(s.toString());
                            BigDecimal amount = market.getPrice().multiply(buy_amount);
                            buy_calc.setText("Total: " + amount.toString() + " " + market.getBaseCoin() + ".");
                        } catch (Exception ex) {
                            // when words are typed.
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            sell_box.setText("1");
            buy_box.setText("1");
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

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.buy_button:
                    mListener.create_transaction(this.email, market_name,
                            buy_amount, "buy");
                    break;
                case R.id.sell_button:
                    mListener.create_transaction(this.email, market_name,
                            sell_amount, "sell");
                    break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void create_transaction(String email, String market_name, BigDecimal quantity, String type);
    }
}
