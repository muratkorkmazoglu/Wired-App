package murat.korkmazoglu.wired;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Content extends AppCompatActivity {

    private String link, image, title;
    ProgressDialog progressDialog;
    private List<String> paragraf;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams llp;
    private TextView titleText;
    private ImageView imageView;
    private String currentLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.content_layout);
        new FetchTitle().execute();

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView = (ImageView) findViewById(R.id.myImageView);
        titleText = (TextView) findViewById(R.id.tv_title);
        llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);

        link = getIntent().getStringExtra("link");
        image = getIntent().getStringExtra("image");
        Log.d("IMAGE", image.toString());
        title = getIntent().getStringExtra("title");
        titleText.setText(title);
        Picasso.with(Content.this).load(image).fit().into(imageView);

    }

    class FetchTitle extends AsyncTask<String, String, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            try {
                paragraf = new ArrayList<String>();
                Document doc = Jsoup.connect(link).get();
                Elements info = doc.select("p[data-reactid]");
                for (Element p : info)
                    paragraf.add(p.text().toString());

            } catch (IOException ex) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress("Makaleler Okunuyor...");

            return paragraf;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Content.this, "Lütfen Bekleyiniz", "İşlem Devam Ediyor", true);
        }

        @Override
        protected void onPostExecute(final List<String> strings) {
            super.onPostExecute(strings);

            for (int i = 0; i < strings.size(); i++) {
                TextView textView = new TextView(Content.this);
                textView.setText(strings.get(i));
                textView.setLayoutParams(llp);
                textView.setTextColor(Color.BLACK);
                linearLayout.addView(textView);
            }
            progressDialog.cancel();
            Count();


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }
    }

    public void Count() {

        String currentLine = "";
        HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();

        for (int i = 0; i < paragraf.size(); i++) {
            currentLine += paragraf.get(i);
        }
        System.out.println(currentLine.toString());

            String[] words = currentLine.toLowerCase().split(" ");

            for (String word : words) {
                if (word.length() > 2) {
                    if (wordCountMap.containsKey(word)) {
                        wordCountMap.put(word, wordCountMap.get(word) + 1);
                    } else {
                        wordCountMap.put(word, 1);
                    }
                }
            }

        int count = 0;
        String mostRepeatedWord = null;
        Set<Entry<String, Integer>> entrySet = wordCountMap.entrySet();

        for (Entry<String, Integer> entry : entrySet) {
            if (entry.getValue() > count) {
                mostRepeatedWord = entry.getKey();
                count = entry.getValue();
            }
        }

        System.out.println("The most repeated word in input file is : " + mostRepeatedWord);

        System.out.println("Number Of Occurrences : " + count);
    }

}
