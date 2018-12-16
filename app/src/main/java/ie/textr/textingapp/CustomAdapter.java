package ie.textr.textingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<Contacts> arr;

    //Create constructor


    public CustomAdapter(Context context, ArrayList<Contacts> arr) {
        this.context = context;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    } // this is index

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.rowdesign, parent, false);

        TextView num = (TextView) convertView.findViewById(R.id.nums);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView contact_no = (TextView) convertView.findViewById(R.id.mobileno);

        // set data into textviews
        num.setText(" " + arr.get(position).getNum());
        name.setText(arr.get(position).getName());
        contact_no.setText(arr.get(position).getMobile_no());

        return convertView;
    }
}
