package util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Doseon on 2/25/2018.
 */

public class Posts {
    public HashMap<String, String> postSet;

    public String url;

    /**
     * Initializes PostParams Object with:
     * @param map Map of user parameters.
     */
    public Posts(HashMap<String, String> map) {
        this(map, "");
    }

    public Posts(HashMap<String, String> map, String url) {
        this.postSet = map;
        this.url = url;
    }

    /**
     * Returns Post data String.
     * @param params map of parameters.
     * @return String Post data.
     */
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
