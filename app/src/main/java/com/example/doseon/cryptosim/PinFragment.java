package com.example.doseon.cryptosim;


        import android.view.LayoutInflater;
        import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;


/**
 * PinFragment that holds Pin Fragment page
 * where user types his 6-digit pin code in order
 * to register.
 */
public class PinFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * User email.
     */
    private String user;

    /**
     * User password.
     */
    private String pass;

    /**
     * User name.
     */
    private String name;

    //Registration Pin or Forgot Password Pin.
    private Boolean forgot;


    /**
     * Initializes pinFragment fields with default values.
     */
    public PinFragment() {
        user = "";
        pass = "";
        name = "";
        forgot = false;
    }

    /**
     * Sets pinFragment fields with passed values.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            user = getArguments().getString(getString(R.string.email_key));
            pass = getArguments().getString(getString(R.string.password_key));
            name = getArguments().getString(getString(R.string.name_key));
            forgot = getArguments().getBoolean("CHANGE_PASS_BOOLEAN");
        }
    }

    /**
     * Creates view of pinFragment and initializes submit pin button.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View.
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pin, container, false);

        Button pinSubmitButton = (Button) v.findViewById(R.id.pin_submit_button);
        pinSubmitButton.setOnClickListener(this);

        return v;
    }

    /**
     * Manages submit pin button of PinFragment.
     * @param view pinFragment View.
     */
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            switch(view.getId()) {
                case R.id.pin_submit_button:
                    mListener.submitPin(user, pass, name, forgot);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Calls corresponding method from LoginActivity.
     */
    public interface OnFragmentInteractionListener {

        void submitPin(String user, String pass, String name, Boolean forgot);

    }

}