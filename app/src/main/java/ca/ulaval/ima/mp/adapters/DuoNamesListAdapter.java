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
import ca.ulaval.ima.mp.game.Player;

public class DuoNamesListAdapter extends ArrayAdapter<Player> {
    private LayoutInflater inflater;

    public DuoNamesListAdapter(Context context, List<Player> data) {
        super(context, -1, data);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        Player p = getItem(position).getVote();
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_duo_nameslist, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.check = convertView.findViewById(R.id.check);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.name.setText(name);
        if (p != null)
            viewHolder.check.setImageResource(R.drawable.toggle_checked);
        return convertView;
    }

    private static class ViewHolder {
        ImageView check;
        TextView name;
    }
}