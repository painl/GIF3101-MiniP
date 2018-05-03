package ca.ulaval.ima.mp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.activities.MainActivity;
import ca.ulaval.ima.mp.application.BlueGarouApplication;
import ca.ulaval.ima.mp.network.WSResponse;

public class AuthFragment extends AbstractFragment {

    private AUTH mAuth;
    private EditText mUser;
    private EditText mPass;
    private EditText mPassConfirm;
    private TextView mError;
    private TextView mNeed;
    private Button mBtn1;
    private Button mBtn2;
    private AlertDialog alert;

    public enum AUTH {
        REGISTER,
        LOGIN
    }

    public static AuthFragment newInstance(AUTH auth)
    {
        AuthFragment authFragment = new AuthFragment();
        authFragment.mAuth = auth;
        authFragment.setLayout(R.layout.fragment_auth);
        return authFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUser = mView.findViewById(R.id.user);
        mPass = mView.findViewById(R.id.pass);
        mPassConfirm = mView.findViewById(R.id.pass_confirm);
        mError = mView.findViewById(R.id.error);
        mNeed = mView.findViewById(R.id.need);
        mBtn1 = mView.findViewById(R.id.btn_1);
        mBtn2 = mView.findViewById(R.id.btn_2);
        initView();
    }

    private void alertLoaded(final int success, String message)
    {
        TextView text = alert.findViewById(R.id.text);
        alert.findViewById(R.id.progress).setVisibility(View.GONE);
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        if (success == 0)
            text.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
        alert.findViewById(R.id.text).setVisibility(View.VISIBLE);
        text.setText(message);
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                if (success == 1)
                    ((MainActivity)mContext).onBackPressed();
            }
        });
    }

    private void requestDialog()
    {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogView = inflater.inflate(R.layout.dialog_request_load, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alert = builder.create();
        alert.setCancelable(false);
        alert.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    private void validate(String user, String pass)
    {
        if (user.length() < 4)
        {
            mError.setText("Le nom d'utilisateur doit faire au moins 4 caractères.");
            mError.setVisibility(View.VISIBLE);
        }
        else if (pass.length() < 4)
        {
            mError.setText("Le mot de passe doit faire au moins 4 caractères.");
            mError.setVisibility(View.VISIBLE);
        }
        else {
            mError.setVisibility(View.GONE);
            requestDialog();
            try {
                BlueGarouApplication.getInstance().getWebService().POSTRequest("login", onLogin(), new JSONObject().put("username", user).put("password", pass));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void validate(String user, String pass, String passConfirm)
    {
        if (user.length() < 4)
        {
            mError.setText("Le nom d'utilisateur doit faire au moins 4 caractères.");
            mError.setVisibility(View.VISIBLE);
        }
        else if (pass.length() < 4)
        {
            mError.setText("Le mot de passe doit faire au moins 4 caractères.");
            mError.setVisibility(View.VISIBLE);
        }
        else if (!pass.equals(passConfirm))
        {
            mError.setText("Le mot de passe ne correspond pas.");
            mError.setVisibility(View.VISIBLE);
        }
        else {
            mError.setVisibility(View.GONE);
            requestDialog();
            try {
                BlueGarouApplication.getInstance().getWebService().POSTRequest("register", onRegister(), new JSONObject().put("username", user).put("password", pass));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView()
    {
        mError.setVisibility(View.GONE);
        if (mAuth == AUTH.LOGIN)
        {
            mPassConfirm.setVisibility(View.GONE);
            mNeed.setVisibility(View.VISIBLE);
            mBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validate(mUser.getText().toString(), mPass.getText().toString());
                }
            });
            mBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mContext).fragmentTransit(AuthFragment.newInstance(AUTH.REGISTER), true);
                }
            });
        }
        else
        {
            mPassConfirm.setVisibility(View.VISIBLE);
            mNeed.setVisibility(View.GONE);
            mBtn1.setText("S'inscrire");
            mBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validate(mUser.getText().toString(), mPass.getText().toString(), mPassConfirm.getText().toString());
                }
            });
            mBtn2.setText("Retour");
            mBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mContext).fragmentTransit(AuthFragment.newInstance(AUTH.LOGIN), false);
                }
            });
        }
    }

    private WSResponse<JSONObject> onLogin() {
        return new WSResponse<>(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.d("GET_OBJX", response.toString());
                alertLoaded(1, "Connexion réussie");
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("BLUEGAROU", Context.MODE_PRIVATE);
                try {
                    sharedPreferences.edit().putString("access_token", response.getJSONObject("access_token").getString("token")).commit();
                    BlueGarouApplication.getInstance().setAuth(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET_OBJX", error.toString());
                alertLoaded(0, "Erreur de connexion");
            }
        });
    }

    private WSResponse<JSONObject> onRegister() {
        return new WSResponse<>(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                Log.d("GET_OBJX", response.toString());
                alertLoaded(1, "Inscription réussie");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET_OBJX", error.toString());
                alertLoaded(0, "Erreur d'inscription");
            }
        });
    }
}
