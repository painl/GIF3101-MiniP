package ca.ulaval.ima.mp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.application.BlueGarouApplication;
import ca.ulaval.ima.mp.fragments.HomeFragment;
import ca.ulaval.ima.mp.fragments.RulesFragment;
import ca.ulaval.ima.mp.fragments.StatsFragment;
import ca.ulaval.ima.mp.network.WSResponse;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mFragment;
    private AlertDialog alert;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment currentFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    currentFragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_stats:
                    currentFragment = StatsFragment.newInstance();
                    break;
                case R.id.navigation_rules:
                    currentFragment = RulesFragment.newInstance();
                    break;
            }
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return fragmentTransit(currentFragment, false);
        }
    };

    public boolean fragmentTransit(Fragment transit, boolean toBackStack) {
        if (transit != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(mFragment.getId(), transit, transit.getClass().getSimpleName());
            if (toBackStack)
                transaction.addToBackStack(transit.getClass().getSimpleName());
            transaction.commit();
            return true;
        }
        return false;
    }

    private void tryAuth() {
        SharedPreferences sharedPreferences = BlueGarouApplication.getInstance().getSharedPreferences("BLUEGAROU", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("access_token", null);
        if (token != null) {
            Log.d("TOKKKK", token);
            BlueGarouApplication.getInstance().setAuth(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tryAuth();
        mFragment = findViewById(R.id.main_content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment currentFragment = HomeFragment.newInstance();
        fragmentTransit(currentFragment, false);
    }
}
