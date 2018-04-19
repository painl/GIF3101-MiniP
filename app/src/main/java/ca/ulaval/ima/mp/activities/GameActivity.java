package ca.ulaval.ima.mp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import ca.ulaval.ima.mp.fragments.LobbyFragment;

public class GameActivity extends AppCompatActivity {

    private FrameLayout mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = findViewById(android.R.id.content);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mFragment.getId(), LobbyFragment.newInstance());
        ft.commit();
    }

    public boolean fragmentTransit(Fragment transit, boolean toBackStack)
    {
        if (transit != null)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(mFragment.getId(), transit, transit.getClass().getSimpleName());
            if (toBackStack)
                transaction.addToBackStack(transit.getClass().getSimpleName());
            transaction.commit();
            return true;
        }
        return false;
    }
}
