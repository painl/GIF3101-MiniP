package ca.ulaval.ima.mp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ulaval.ima.mp.R;

public class RemoteListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> data;

    public RemoteListAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.adapter_remotelist, values);
        this.mContext = context;
        this.data = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_remotelist, parent, false);
        TextView textView = rowView.findViewById(R.id.txt);
        textView.setText(data.get(position));
        return rowView;
    }
}