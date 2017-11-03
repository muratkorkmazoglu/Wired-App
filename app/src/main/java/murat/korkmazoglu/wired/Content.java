package murat.korkmazoglu.wired;


import android.app.ProgressDialog;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class Content extends AppCompatActivity {

    private String link, image, title;
    private ProgressDialog progressDialog;
    private List<String> paragraf;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams llp;
    private TextView titleText, tvCount1, tvCount2, tvCount3, tvCount4, tvCount5;
    private TextView tvEnglish1, tvEnglish2, tvEnglish3, tvEnglish4, tvEnglish5;
    private TextView tvTurkish1, tvTurkish2, tvTurkish3, tvTurkish4, tvTurkish5;
    private ImageView imageView;
    private String text1, text2, text3, text4, text5;
    private int text1Count, text2Count, text3Count, text4Count, text5Count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        new FetchTitle().execute();

        tvCount1 = (TextView) findViewById(R.id.tvCount1);
        tvCount2 = (TextView) findViewById(R.id.tvCount2);
        tvCount3 = (TextView) findViewById(R.id.tvCount3);
        tvCount4 = (TextView) findViewById(R.id.tvCount4);
        tvCount5 = (TextView) findViewById(R.id.tvCount5);

        tvEnglish1 = (TextView) findViewById(R.id.tvEnglish1);
        tvEnglish2 = (TextView) findViewById(R.id.tvEnglish2);
        tvEnglish3 = (TextView) findViewById(R.id.tvEnglish3);
        tvEnglish4 = (TextView) findViewById(R.id.tvEnglish4);
        tvEnglish5 = (TextView) findViewById(R.id.tvEnglish5);

        tvTurkish1 = (TextView) findViewById(R.id.tvTurkish1);
        tvTurkish2 = (TextView) findViewById(R.id.tvTurkish2);
        tvTurkish3 = (TextView) findViewById(R.id.tvTurkish3);
        tvTurkish4 = (TextView) findViewById(R.id.tvTurkish4);
        tvTurkish5 = (TextView) findViewById(R.id.tvTurkish5);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView = (ImageView) findViewById(R.id.myImageView);
        titleText = (TextView) findViewById(R.id.tv_title);
        llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);

        link = getIntent().getStringExtra("link");
        image = getIntent().getStringExtra("image");
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
                Elements info = doc.select("p");
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
            if (word.length() > 3) {
                if (wordCountMap.containsKey(word)) {
                    wordCountMap.put(word, wordCountMap.get(word) + 1);
                } else {
                    wordCountMap.put(word, 1);
                }
            }
        }

        Set<Entry<String, Integer>> entrySet = wordCountMap.entrySet();

        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(entrySet);

        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                return (e2.getValue().compareTo(e1.getValue()));
            }
        });

        System.out.println("Repeated Words In Input File Are :");
        ArrayList<Map.Entry<String, Integer>> arr = new ArrayList<Map.Entry<String, Integer>>();

        for (Entry<String, Integer> entry : list) {
            if (entry.getValue() > 1) {
                arr.add(entry);
            }
        }
        text1 = arr.get(0).getKey().toString();
        text1Count = arr.get(0).getValue();
        text2 = arr.get(1).getKey().toString();
        text2Count = arr.get(1).getValue();
        text3 = arr.get(2).getKey().toString();
        text3Count = arr.get(2).getValue();
        text4 = arr.get(3).getKey().toString();
        text4Count = arr.get(3).getValue();
        text5 = arr.get(4).getKey().toString();
        text5Count = arr.get(4).getValue();

        String languagePair = "en-tr";

        String translate1 = Translate(text1, languagePair);
        String translate2 = Translate(text2, languagePair);
        String translate3 = Translate(text3, languagePair);
        String translate4 = Translate(text4, languagePair);
        String translate5 = Translate(text5, languagePair);

        tvCount1.setText(String.valueOf(text1Count));
        tvCount2.setText(String.valueOf(text2Count));
        tvCount3.setText(String.valueOf(text3Count));
        tvCount4.setText(String.valueOf(text4Count));
        tvCount5.setText(String.valueOf(text5Count));


        tvEnglish1.setText(text1);
        tvEnglish2.setText(text2);
        tvEnglish3.setText(text3);
        tvEnglish4.setText(text4);
        tvEnglish5.setText(text5);

        tvTurkish1.setText(translate1);
        tvTurkish2.setText(translate2);
        tvTurkish3.setText(translate3);
        tvTurkish4.setText(translate4);
        tvTurkish5.setText(translate5);
        progressDialog.cancel();

    }

    public String Translate(String textToBeTranslated, String languagePair) {
        String translationResult = null;
        TranslatorBackgroundTask translatorBackgroundTask = new TranslatorBackgroundTask(getApplicationContext());
        try {
            translationResult = translatorBackgroundTask.execute(textToBeTranslated, languagePair).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return translationResult;

    }

}
