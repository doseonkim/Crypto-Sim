package util;

/**
 * Created by Doseon on 2/25/2018.
 */

public class Links {

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

    // Web Service link to register a user.
    public static final String STORE_ACC_URL
            =  "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/register.php";
    //= "http://farmfresh.getenjoyment.net/register.php";

    // Web service link to verify if the username and password match.
    public static final String VERIFY_ACC_URL
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/confirm_info.php";
    //= "http://farmfresh.getenjoyment.net/confirm_info.php";

    // Web service to check if the username exists in the database.
    public static final String CHECK_USER_URL
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/check_user.php?user=";
    //= "http://farmfresh.getenjoyment.net/check_user.php?user=";

    // Web service to send email to user.
    public static final String SEND_EMAIL_URL
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/send_email.php";

    // Web service to confirm the generated pin for the specific account.
    public static final String CONFIRM_PIN_URL
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/confirm_pin.php";

    // Web service to change the password of a current account.
    public static final String CHANGE_PASS_URL
            = "http://cssgate.insttech.washington.edu/~doseon/CryptoSim/change_pass.php";

}
