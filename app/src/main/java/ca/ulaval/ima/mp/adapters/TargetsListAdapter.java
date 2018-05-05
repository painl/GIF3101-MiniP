package ca.ulaval.ima.mp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.ulaval.ima.mp.R;

public class TargetsListAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;
    private int selectedIndex = -1;

    public TargetsListAdapter(Context context, List<String> data) {
        super(context, -1, data);
        inflater = LayoutInflater.from(context);
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_duo_nameslist, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.check = convertView.findViewById(R.id.check);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(name);
        if (selectedIndex == position)
            viewHolder.check.setImageResource(R.drawable.toggle_checked);
        else
            viewHolder.check.setImageResource(R.drawable.toggle_unchecked);
        return convertView;
    }

    private static class ViewHolder {
        ImageView check;
        TextView name;
    }
}