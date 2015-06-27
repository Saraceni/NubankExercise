package app.nubankexercise.com.nubankexercise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nubankexercise.com.nubankexercise.MonthFragment;
import app.nubankexercise.com.nubankexercise.R;

/**
 * Created by rafaelgontijo on 6/26/15.
 */
public class LineItemsAdapter extends BaseAdapter {

    private JSONArray items;
    private Context context;

    public LineItemsAdapter(Context context, JSONArray items)
    {
        super();
        this.context = context;
        this.items = items;
    }

    public int getCount()
    {
        return items.length();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view == null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.item_adapter_layout, null);
        }

        TextView tvDescription = (TextView) view.findViewById(R.id.adapter_description);
        TextView tvDate = (TextView) view.findViewById(R.id.adapter_date);
        TextView tvPrice = (TextView) view.findViewById(R.id.adapter_price);

        try
        {
            JSONObject lineItem = items.getJSONObject(position);
            String postDate = lineItem.getString("post_date");
            String formattedPostDate[] = MonthFragment.returnFormattedDate(postDate);
            tvDate.setText(formattedPostDate[0] + " " + formattedPostDate[1].substring(0,3));
            String description = lineItem.getString("title");
            tvDescription.setText(description);
            String amount = lineItem.getString("amount");
            if(amount.contains("-"))
            {
                amount = amount.substring(1);
                tvPrice.setTextColor(context.getResources().getColor(R.color.color_overdue));
            }
            String formatedAmount = MonthFragment.returnFormattedPrice(amount);
            tvPrice.setText(formatedAmount.substring(3));


        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }

        return view;
    }



}
