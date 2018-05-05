package ca.ulaval.ima.mp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ulaval.ima.mp.R;

public class LobbyListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> data;

    public LobbyListAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.adapter_lobbylist, values);
        this.mContext = context;
        this.data = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_lobbylist, parent, false);
        TextView textView = rowView.findViewById(R.id.txt);
        textView.setText(data.get(position));

        ImageButton deleteBtn = rowView.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }

    public ArrayList<String> getData() {
        return data;
    }
}