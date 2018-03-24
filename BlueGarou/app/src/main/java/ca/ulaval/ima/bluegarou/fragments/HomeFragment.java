package ca.ulaval.ima.bluegarou.fragments;


import ca.ulaval.ima.bluegarou.R;

public class HomeFragment extends AbstractFragment {

    public static AbstractFragment newInstance()
   {
        AbstractFragment homeFragment = AbstractFragment.newInstance(R.layout.fragment_home);
        return homeFragment;
   }
}
