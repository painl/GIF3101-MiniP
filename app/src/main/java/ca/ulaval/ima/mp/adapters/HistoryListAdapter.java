package ca.ulaval.ima.mp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.ima.mp.R;

public class HistoryListAdapter extends ArrayAdapter<JSONObject> {
    private final Context mContext;
    private final JSONArray stats;

    public HistoryListAdapter(Context context, JSONArray stats) {
        super(context, R.layout.adapter_historylist);
        this.mContext = context;
        this.stats = stats;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            JSONObject stat = stats.getJSONObject(position);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.adapter_historylist, parent, false);

            if (convertView == null) {
                convertView = rowView;
            }
            TextView textView = rowView.findViewById(R.id.winning_side);
            int winningName = (stat.getString("winningside").compareTo("WEREWOLVES") == 0) ?
                    R.string.completestr_werewolves : R.string.completestr_villagers;

            TextView textView2 = rowView.findViewById(R.id.werewolves_list);
            JSONArray wwolves = stat.getJSONArray("werewolves");
            List<String> wolveslist = new ArrayList<>();
            for (int i = 0; i < wwolves.length(); i++){ wolveslist.add(wwolves.getString(i)); }

            TextView textView3 = rowView.findViewById(R.id.villagers_list);
            JSONArray villagers = stat.getJSONArray("villagers");
            List<String> villagerslist = new ArrayList<>();
            for (int i = 0; i < villagers.length(); i++){ villagerslist.add(villagers.getString(i)); }

            textView.setText(mContext.getResources().getString(winningName));
            textView2.setText(TextUtils.join(",", wolveslist));
            textView3.setText(TextUtils.join(",", villagerslist));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getCount(){
        return stats.length();
    }
}