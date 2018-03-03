package util;

/**
 * Created by Doseon on 2/25/2018.
 */

public class Links {

    // Web Service link to get all Coin table database in JSON.
    public static final String GET_COINS_LINK
            =  "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/get_coin_list.php";

    // Web Service link to get all Market table database in JSON.
    public static final String GET_MARKET_LINK
            =  "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/get_market_list.php";

    // Web service link to update prices in the database.
    public static final String UPDATE_PRICES_LINK
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/update_coin_data.php";

    public static final String PRICE_API_LINK
            = "https://api.coinmarketcap.com/v1/ticker/";

    public static final String BITTREX_API_LINK
            = "https://bittrex.com/api/v1.1/public/getmarketsummary?market=";

    public static final String UPDATE_PRICES_DB_LINK
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/update_market_data.php";

    public static final String TRANSACTION_LINK
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/create_transaction.php";

    public static final String GET_WALLET_LINK
            =  "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/get_user_wallet.php";

    public static final String GET_TRANSACTION_LINK
            =  "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/get_user_transaction.php";

}
