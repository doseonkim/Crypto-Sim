package com.example.doseon.cryptosim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.regex.Pattern;

import handler.ConfirmPinPostAsync;
import handler.ForgotPassAsync;
import handler.NoHandlePostAsync;
import handler.RegistrationRequirementsHandler;
import handler.SendEmailPostAsync;
import util.Posts;

import static util.Links.CHANGE_PASS_URL;
import static util.Links.SEND_EMAIL_URL;
import static util.Links.STORE_ACC_URL;

public class RegistrationActivity extends AppCompatActivity implements
        RegisterFragment.OnFragmentInteractionListener, PinFragment.OnFragmentInteractionListener,
        ChangePassFragment.OnFragmentInteractionListener {

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {

                mPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);

                int reg = this.getIntent().getIntExtra("REGISTRATION_CODE", 0);
                if (reg == 1) {
                    RegisterFragment rf = new RegisterFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainer, rf)
                            .commit();
                } else {
                    String email = this.getIntent().getStringExtra("EMAIL_FOR_FORGOTTEN");
                    Bundle args = new Bundle();
                    args.putSerializable(getString(R.string.email_key), email);
                    args.putSerializable("CHANGE_PASS_BOOLEAN", true);


                    PinFragment pf = new PinFragment();
                    pf.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                                    .add(R.id.fragmentContainer, pf)
                                    .commit();
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = (new Intent(this, LoginActivity.class));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Checks user entered information during registration for validity.
     * If valid -> Redirects to pinFragment.
     */
    @Override
    public void goPin() {
        EditText name_text = (EditText) findViewById(R.id.registration_name);
        EditText email_text = (EditText) findViewById(R.id.registration_email);
        EditText pass_text = (EditText) findViewById(R.id.registration_pass);
        EditText confirm_text = (EditText) findViewById(R.id.registration_confirm);

        RegistrationRequirementsHandler regHandle = new
                RegistrationRequirementsHandler(name_text, email_text, pass_text, confirm_text);
        if (regHandle.checkRegistrationErrors()) {

            HashMap<String, String> params = new HashMap<>();
            params.put("name", name_text.getText().toString());
            params.put("email", email_text.getText().toString());
            Posts pm = new Posts(params, SEND_EMAIL_URL);

            //SEND EMAIL ASYNC POST
            SendEmailPostAsync sendEmail = new SendEmailPostAsync(this,
                    email_text.getText().toString(), pm);
            sendEmail.execute();
        }
    }

    /**
     * Submit pin button handler.
     * Checks pin for validity and if valid then
     * redirects to LoginFragment.
     * @param email user email.
     * @param pass user password.
     * @param name user name.
     */
    @Override
    public void submitPin(String email, String pass, String name, Boolean forgot) {
        EditText pin_text = (EditText) findViewById(R.id.pin_edit_text);

        if (pin_text.getText().toString().length() < 6) {
            pin_text.setError("You must type in a 6 pin code.");
        } else {
            //CHECK IF PIN IS CORRECT THROUGH ASYNC.
            HashMap<String, String> params = new HashMap<>();
            params.put("user", email);
            params.put("pin", pin_text.getText().toString());
            Posts pm = new Posts(params, STORE_ACC_URL);

            ConfirmPinPostAsync confirmPin = new ConfirmPinPostAsync(this, pm, email, pass,
                    name, forgot, mPrefs);
            confirmPin.execute();

        }
    }

    /**
     * Check if password values are valid.
     * Sends POST request to change password
     * to backend.
     * @param email User email.
     */
    @Override
    public void onChangePass(String email) {
        EditText new_pass = (EditText) findViewById(R.id.change_pass);
        EditText new_pass_confirm = (EditText) findViewById(R.id.change_pass_confirm);
        boolean canProceed = true;
        if (TextUtils.isEmpty(new_pass.getText())) {
            new_pass.setError("You must type in a password.");
            canProceed = false;
        }
        //Password must be at least 8 characters long.
        if (!Pattern.matches("\\S{8,}", new_pass.getText())) {
            new_pass.setError("Password must be at least 8 characters long.");
            canProceed = false;
        }
        if (TextUtils.isEmpty(new_pass_confirm.getText())) {
            new_pass_confirm.setError("You must type in a password.");
            canProceed = false;
        }

        if (canProceed && !new_pass_confirm.getText().toString().equals(
                new_pass.getText().toString())) {
            canProceed = false;
            new_pass.setError("Your password does not match.");
            new_pass_confirm.setError("Your password does not match.");
        }

        if (canProceed) {
            HashMap<String, String> params = new HashMap<>();
            params.put("user", email);
            params.put("pass", new_pass.getText().toString());
            Posts pm = new Posts(params, CHANGE_PASS_URL);
            NoHandlePostAsync saveInfoTask = new NoHandlePostAsync(this, pm);
            saveInfoTask.execute();
            //GO BACK TO LOGIN FRAGMENT AND OPEN TOASTER.

            saveToSharedPrefs(email, new_pass.toString(), 0, 0);

            Bundle args = new Bundle();

            args.putString(getString(R.string.SAVEDNAME), mPrefs.getString(getString(R.string.SAVEDNAME), ""));
            args.putString(getString(R.string.SAVEDPASS), mPrefs.getString(getString(R.string.SAVEDPASS), ""));
            args.putInt(getString(R.string.SAVEDAUTO), mPrefs.getInt(getString(R.string.SAVEDAUTO), 0));
            args.putSerializable("LOGIN_MESSAGE", "You have successfully changed your password.");

            Intent intent = (new Intent(this, LoginActivity.class));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Saves user credentials to shared preferences.
     * @param name username
     * @param pass user password.
     * @param auto
     */
    public void saveToSharedPrefs(String name, String pass, Integer auto, Integer admin) {
        mPrefs.edit().putString(getString(R.string.SAVEDNAME), name).apply();
        mPrefs.edit().putString(getString(R.string.SAVEDPASS), pass).apply();
        mPrefs.edit().putInt(getString(R.string.SAVEDAUTO), auto).apply();
        mPrefs.edit().putInt(getString(R.string.SAVEDADMIN), admin).apply();
    }

}
