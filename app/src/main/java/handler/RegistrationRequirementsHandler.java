package handler;

/**
 * Created by Doseon on 3/2/2018.
 */

import android.text.TextUtils;
import android.widget.EditText;

        import android.text.TextUtils;
        import android.widget.EditText;

        import java.util.regex.Pattern;

/**
 * Created by Doseo on 11/3/2017.
 * Edited by Alisher.
 * Class for handling Registration requirements.
 * Checks User entered registration credentials for validity.
 * Requirements are:
 * valid email;
 * password at least 8 characters in length (non-whitespace);
 * confirm password and password must match.
 */
public class RegistrationRequirementsHandler {

    /**
     * Email regex.
     */
    private static final String EMAIL_REGEX
            = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+" +
            "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
            "|\"(?:[\\x01-x08\\x0b\\x0c\\x0e-\\x1f" +
            "\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-" +
            "\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*" +
            "[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]" +
            "[0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]" +
            "?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-x08\\x0b\\x0c\\x0e-\\" +
            "x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    /**
     * user name EditText.
     */
    private EditText name_text;

    /**
     * user email EditText.
     */
    private EditText email_text;

    /**
     * user password EditText.
     */
    private EditText pass_text;

    /**
     * user confirm password EditText.
     */
    private EditText confirm_text;

    /**
     * Creates RegistrationRequirementsHandler Object.
     * @param name_text user name.
     * @param email_text user email.
     * @param pass_text user password.
     * @param confirm_text user confirm password.
     */
    public RegistrationRequirementsHandler(EditText name_text, EditText email_text, EditText pass_text,
                                           EditText confirm_text) {
        this.name_text = name_text;
        this.email_text = email_text;
        this.pass_text = pass_text;
        this.confirm_text = confirm_text;
    }

    /**
     * Checks Registration Credentials of user.
     * Requirements are:
     * valid email;
     * password at least 8 characters in length (non-whitespace);
     * confirm password and password must match.
     * @return true if entered credentials are valid,
     * false otherwise.
     */
    public boolean checkRegistrationErrors() {
        boolean canProceed = true;

        // Check if username already exists WITH GET.
        // Check password length username length etc.
        // Check if it is an actual email with @
        if (TextUtils.isEmpty(name_text.getText())) {
            name_text.setError("You must type in a name.");
            canProceed = false;
        }
        if (TextUtils.isEmpty(email_text.getText())) {
            email_text.setError("You must type in a email.");
            canProceed = false;
        }
        //Email must be valid.
        if (!Pattern.matches(EMAIL_REGEX, email_text.getText().toString().toLowerCase())) {
            email_text.setError("Email must be valid.");
            canProceed = false;
        }
        if (TextUtils.isEmpty(pass_text.getText())) {
            pass_text.setError("You must type in a password.");
            canProceed = false;
        }
        //Password must be at least 8 characters long.
        if (!Pattern.matches("\\S{8,}", pass_text.getText())) {
            pass_text.setError("Password must be at least 8 characters long.");
            canProceed = false;
        }
        if (TextUtils.isEmpty(confirm_text.getText())) {
            confirm_text.setError("You must type in a password.");
            canProceed = false;
        }

        if (canProceed && !confirm_text.getText().toString().equals(
                pass_text.getText().toString())) {
            canProceed = false;
            pass_text.setError("Your password does not match.");
            confirm_text.setError("Your password does not match.");
        }
        return canProceed;
    }
}