package ca.ulaval.ima.bluegarou.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import ca.ulaval.ima.bluegarou.R;
import ca.ulaval.ima.bluegarou.fragments.HomeFragment;
import ca.ulaval.ima.bluegarou.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment currentFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    currentFragment = HomeFragment.newInstance();
                    break ;
                case R.id.navigation_dashboard:
                    currentFragment = HomeFragment.newInstance();
                    break ;
                case R.id.navigation_notifications:
                    currentFragment = HomeFragment.newInstance();
                    break ;
                case R.id.navigation_settings:
                    currentFragment = SettingsFragment.newInstance();
                    break ;
            }
            if (currentFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(mFragment.getId(), currentFragment, currentFragment.getClass().getSimpleName());
                transaction.commit();
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragment = findViewById(R.id.main_content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment currentFragment = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mFragment.getId(), currentFragment, currentFragment.getClass().getSimpleName());
        transaction.commit();
    }

}
