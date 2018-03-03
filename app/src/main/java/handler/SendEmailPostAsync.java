package handler;

        import android.widget.Toast;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.example.doseon.cryptosim.LoginActivity;
        import com.example.doseon.cryptosim.PinFragment;
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

        import static util.Links.CHECK_USER_URL;
        import static util.Posts.getPostDataString;

/**
 * Thread that sends pin code to user email.
 * Created by Doseon on 11/8/2017.
 */


public class SendEmailPostAsync extends AsyncTask<Void, Void, String> {

    /**
     * LoginActivity.
     */
    RegistrationActivity activity;

    /**
     * User email.
     */
    String user_email;

    /**
     * PostParams Object.
     */
    Posts post;

    /**
     * Construct SendEmailPostAsync object.
     * Initializes:
     * @param activity LoginActivity.
     * @param user_email User email.
     * @param params PostParams object.
     */
    public SendEmailPostAsync(RegistrationActivity activity, String user_email, Posts params) {
        this.activity = activity;
        this.user_email = user_email;
        this.post = params;
    }

    /**
     * Establishes connection and gets JSON string.
     * @param string void
     * @return JSON String.
     */
    @Override
    protected String doInBackground(Void... string) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL urlObject = new URL(CHECK_USER_URL + user_email);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
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
        response = response;
        //Log.d("POST_RESPONse", response);
        return response;
    }

    /**
     * Depending on response:
     * Prompt that username already exists;
     * Proceed next.
     * @param response JSON String.
     */
    @Override
    protected void onPostExecute(String response) {
        // Something wrong with the network or the URL.
        if (response.startsWith("Unable to")) {
            Toast.makeText(activity.getApplicationContext(), response, Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            try {
                JSONObject mainObject = new JSONObject(response);
                Integer code = mainObject.getInt("code");
                EditText email_text = (EditText) activity.findViewById(R.id.registration_email);
                if (code == 200) {
                    activity.findViewById(R.id.register_loading).setVisibility(View.GONE);
                    activity.findViewById(R.id.register_submit).setEnabled(true);
                    //Toast.makeText(activity.getApplicationContext(),
                    //        "Username already exists.", Toast.LENGTH_LONG).show();
                    email_text.setError("Email already exists.");
                    return;
                } else if (code == 300){
                    moveToPinPageHandler goToPinTask = new moveToPinPageHandler();
                    goToPinTask.execute();
                }
            } catch (Exception ex) {
                //not JSON RETURNED
            }
        }
    }

    /**
     * Prepares thread for execution.
     * Sets buttons to disabled.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.findViewById(R.id.register_loading).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.register_submit).setEnabled(false);
    }

    /**
     * Class that manages pin code sending.
     * Called from SendEmailPostAsync.
     */
    public class moveToPinPageHandler extends AsyncTask<Void, Void, String> {

        /**
         * Gets and sends JSON Object.
         * @param params PostParams Object.
         * @return JSON String.
         */
        @Override
        public String doInBackground(Void... params) {
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = post.url;
            HashMap<String, String> map = post.postSet;
            try {
                URL urlObject = new URL(post.url);
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
         * depending on response will:
         * successfully register user;
         * or prompt with error in backend.
         * @param response JSON String.
         */
        @Override
        protected void onPostExecute(String response) {
            activity.findViewById(R.id.register_loading).setVisibility(View.GONE);
            activity.findViewById(R.id.register_submit).setEnabled(true);
            // Something wrong with the network or the URL.
            if (response.startsWith("Unable to")) {
                Toast.makeText(activity.getApplicationContext(), response, Toast.LENGTH_LONG)
                        .show();
                return;
            } else {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    Integer code = mainObject.getInt("code");
                    String pin = mainObject.getString("message");
                    EditText email_text = (EditText) activity.findViewById(R.id.registration_email);
                    EditText pass_text;
                    EditText name_text;
                    if (code == 300) {
                        //Log.d("TEST PIN", pin);

                        pass_text = (EditText) activity.findViewById(R.id.registration_pass);
                        name_text = (EditText) activity.findViewById(R.id.registration_name);

                        Bundle args = new Bundle();
                        args.putSerializable(activity.getString(R.string.email_key), email_text.getText().toString());
                        args.putSerializable(activity.getString(R.string.password_key), pass_text.getText().toString());
                        args.putSerializable(activity.getString(R.string.name_key), name_text.getText().toString());

                        PinFragment pf = new PinFragment();
                        pf.setArguments(args);
                        android.support.v4.app.FragmentTransaction transaction =
                                activity.getSupportFragmentManager()
                                        .beginTransaction().replace(R.id.fragmentContainer, pf)
                                        .addToBackStack(null);
                        transaction.commit();

                    } else {
                        Toast.makeText(activity.getApplicationContext(),
                                "Error occurred in the back end. Please Try Again.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception ex) {
                    //not JSON RETURNED
                }
            }
        }
    }
}