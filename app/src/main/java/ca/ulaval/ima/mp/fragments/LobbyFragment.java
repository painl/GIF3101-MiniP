package ca.ulaval.ima.mp.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import ca.ulaval.ima.mp.R;
import ca.ulaval.ima.mp.adapters.LobbyListAdapter;

public class LobbyFragment extends AbstractFragment {

    private ListView playerListView;
    private ListView remotesListView;

    public static LobbyFragment newInstance() {
        LobbyFragment fragment = new LobbyFragment();
        fragment.setLayout(R.layout.fragment_lobby);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerListView = mView.findViewById(R.id.player_list);
        String[] values = new String[] { "Louis", "Raf", "Antoine", "Babsou"};
        LobbyListAdapter lba = new LobbyListAdapter(mContext, values);
        playerListView.setAdapter(lba);
        remotesListView = mView.findViewById(R.id.remotes_list);
        String[] values2 = new String[] { "Tel de raf", "Tel de A"};
        LobbyListAdapter lba2 = new LobbyListAdapter(mContext, values);
        remotesListView.setAdapter(lba);
    }

/*    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    } */
}
