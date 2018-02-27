package util;

import android.util.Log;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Doseon on 2/26/2018.
 */

public class Market implements Serializable {

    String market_name;
    String base_coin;
    String alt_coin;
    BigDecimal price;

    public Market(String market_name, String base_coin, String alt_coin, BigDecimal price) {
        this.market_name = market_name;
        this.base_coin = base_coin;
        this.alt_coin = alt_coin;
        this.price = price;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal value) {
        this.price = value;
    }

    public String getName() { return this.market_name; }

    public String getBaseCoin() { return this.base_coin; }

    public String getAltCoin() { return this.alt_coin; }

    public void printData() {
        StringBuilder sb = new StringBuilder();

        sb.append(market_name + "\n");
        sb.append(price + "\n");
        sb.append(base_coin + "\n");
        sb.append(alt_coin + "\n");

        Log.d("Print Data", sb.toString());
    }

}
