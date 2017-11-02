package murat.korkmazoglu.wired;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kiosk on 07/12/2016.
 */

public class CustomAdapter extends BaseAdapter{

    private Context context;
    private List<NewsModel> modelList;

    public CustomAdapter(Context context, List<NewsModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {

        return modelList.size();
    }

    @Override
    public Object getItem(int position) {

        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout rootView = (LinearLayout) ((Activity) context).
                getLayoutInflater().inflate(R.layout.custom_list, null);

        ImageView resim = (ImageView) rootView.findViewById(R.id.resim);
        TextView date_and_creator = (TextView) rootView.findViewById(R.id.date_and_creator);
        TextView title = (TextView) rootView.findViewById(R.id.title);

        final NewsModel model = modelList.get(position);

        Date date = new Date(model.getDate());
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        Picasso.with(context).load(model.getImage()).fit().into(resim);

        title.setText(model.getTitle());
        date_and_creator.
                setText(String.format("%02d:%02d", date.getHours(), date.getMinutes()) + " | " +
                        df.format(date) + "   |  " +
                        model.getCreator());

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkiAc(model.getTitle(),model.getImage(),model.getLink());
            }
        });

        return rootView;
    }

    private void linkiAc(String title,String image,String link) {

        Intent intent = new Intent(context, Content.class);
        intent.putExtra("title", title);
        intent.putExtra("image", image);
        intent.putExtra("link", link);

        context.startActivity(intent);
    }
}
