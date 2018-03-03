package handler;

/**
 * Created by Doseon on 3/2/2018.
 */

import android.content.Intent;
import android.os.Bundle;

        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.Toast;

import com.example.doseon.cryptosim.ChangePassFragment;
import com.example.doseon.cryptosim.LoginActivity;
import com.example.doseon.cryptosim.R;
import com.example.doseon.cryptosim.RegistrationActivity;

import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.HashMap;

import util.Posts;

import static util.Links.CONFIRM_PIN_URL;
import static util.Links.STORE_ACC_URL;
import static util.Posts.getPostDataString;

/**
 * Thread that runs in background when user submits 6-digit pin.
 * Created by Doseon on 11/9/2017.
 */

public class ConfirmPinPostAsync extends AsyncTask<Void, Void, String> {

    /**
     * Activity passed to this class.
     */
    private RegistrationActivity activity;

    /**
     * Parameters passed:
     * user email, pin;
     * corresponding URL.
     */
    private Posts post;

    /**
     * User email.
     */
    private String email;

    /**
     * User password.
     */
    private String pass;

    /**
     * User name.
     */
    private String name;
    /**
     * Check if register or change pass pin screen.
     */
    private Boolean forgot;


    //Shared Preferences for username, autologin feature.
    private SharedPreferences mpref;

    /**
     * Construct ConfirmPostHandler object.
     * Initializes:
     * @param activity LoginActivity
     * @param post parameters.
     * @param email user email.
     * @param pass user password.
     * @param name user name.
     */
    public ConfirmPinPostAsync(RegistrationActivity activity, Posts post, String email,
                               String pass, String name, boolean forgot, SharedPreferences mpref) {
        this.activity = activity;
        this.post = post;
        this.email = email;
        this.pass = pass;
        this.name = name;
        this.forgot = forgot;
        this.mpref = mpref;
    }

    /**
     * Runs in background.
     * Sends POST request to backend.
     * @param voids no params.
     * @return string response to onPostExecute.
     */
    @Override
    public String doInBackground(Void... voids) {
        String response = "";
        HttpURLConnection urlConnection = null;
        HashMap<String, String> map = post.postSet;
        try {
            URL urlObject = new URL(CONFIRM_PIN_URL);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(map));

            writer.flush();
            writer.close();
            os.close();

            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
        } catch (Exception e) {
            response = "Unable to connect, Reason: "
                    + e.getMessage();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        //Log.d("POST_RESPONse", response);
        return response;
    }

    /**
     * Decides what to do next depending on response.
     * Depending on response can:
     * Successfully register user;
     * Prompt with incorrect pin;
     * Prompt with problem in backend;
     * Prompt with no pin for given email.
     * @param response determines what to do next.
     */
    @Override
    protected void onPostExecute(String response) {
        // Something wrong with the network or the URL.
        activity.findViewById(R.id.pin_loading).setVisibility(View.GONE);
        activity.findViewById(R.id.pin_submit_button).setEnabled(true);
        if (response.startsWith("Unable to")) {
            Toast.makeText(activity.getApplicationContext(), response, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            try {
                JSONObject mainObject = new JSONObject(response);
                Integer code = mainObject.getInt("code");
                EditText pin_text = (EditText) activity.findViewById(R.id.pin_edit_text);
                if (code == 300) {
                    //CORRECT PIN
                    correctPinEntered();
                } else if (code == 200) {
                    //Toast.makeText(activity.getApplicationContext(),
                    //        "No pin was found for that username.", Toast.LENGTH_LONG).show();
                    pin_text.setError("No pin for email.");
                    return;
                } else if (code == 201) {
                    //Toast.makeText(activity.getApplicationContext(),
                    //       "Incorrect pin, please try again.", Toast.LENGTH_LONG).show();
                    pin_text.setError("Incorrect pin.");
                    return;
                } else {
                    Toast.makeText(activity.getApplicationContext(),
                            "Back end error, please try again later.", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception ex) {
                //not JSON RETURNED
            }
        }
    }

    /**
     * Pre thread initializations of SearchFragment.
     * Sets button disabled.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.findViewById(R.id.pin_loading).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.pin_submit_button).setEnabled(false);
    }

    /**
     * Called when pin entered is correct.
     * User successfully registered and now is redirected
     * to login page. Saves user credentials.
     */
    private void correctPinEntered() {
        if (!forgot) {
            HashMap<String, String> params = new HashMap<>();
            params.put("user", email);
            params.put("pass", pass);
            params.put("name", name);
            Posts pt = new Posts(params, STORE_ACC_URL);
            NoHandlePostAsync saveInfoTask = new NoHandlePostAsync(activity, pt);
            saveInfoTask.execute();

            //GO BACK TO LOGIN FRAGMENT AND OPEN TOASTER.

            Bundle args = new Bundle();
            //args.putSerializable(activity.getString(R.string.email_key), email);

            activity.saveToSharedPrefs(email, pass, 0);
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();

        } else {
            Bundle args = new Bundle();
            args.putSerializable(activity.getString(R.string.email_key), email);
            ChangePassFragment cpf = new ChangePassFragment();
            cpf.setArguments(args);
            android.support.v4.app.FragmentTransaction transaction = activity.
                    getSupportFragmentManager()
                    .beginTransaction().replace(R.id.fragmentContainer, cpf)
                    .addToBackStack(null);
            transaction.commit();
        }
    }

}