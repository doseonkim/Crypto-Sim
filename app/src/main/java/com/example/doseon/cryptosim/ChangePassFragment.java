package com.example.doseon.cryptosim;


import android.view.LayoutInflater;
import android.view.View;
        import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;


/**
 * ChangePassFragment that hold ChangePassFragment page
 * where user changes the password.
 * Activities that contain this fragment must implement the
 * {@link ChangePassFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChangePassFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * User email.
     */
    private String email;

    /**
     * Constructs ChangePassFragment Object
     * Initializes email to default value.
     */
    public ChangePassFragment() {
        email = "EMPTY_EMAIL";
    }

    /**
     * Sets email to user's email.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            email = getArguments().getString(getString(R.string.email_key));
        }
    }

    /**
     * Creates and returns view of ChangePassFragment
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_pass, container, false);

        Button pinSubmitButton = (Button) v.findViewById(R.id.change_password_button);
        pinSubmitButton.setOnClickListener(this);

        return v;
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

    /**
     * Handles clicking on change password button.
     * @param v View.
     */
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch(v.getId()) {
                case R.id.change_password_button:
                    mListener.onChangePass(this.email);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onChangePass(String email);
    }
}