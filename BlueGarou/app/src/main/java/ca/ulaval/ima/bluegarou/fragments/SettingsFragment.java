package ca.ulaval.ima.bluegarou.fragments;


import ca.ulaval.ima.bluegarou.R;

public class SettingsFragment extends AbstractFragment {

    public static AbstractFragment newInstance()
   {
        AbstractFragment settingsFragment = AbstractFragment.newInstance(R.layout.fragment_settings);
        return settingsFragment;
   }
}
