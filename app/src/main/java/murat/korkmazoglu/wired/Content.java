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


public class Content extends AppCompatActivity {

    private String link, image, title;
    private ProgressDialog progressDialog;
    private List<String> paragraf;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams llp;
    private TextView titleText;
    private ImageView imageView;
    private String text1, text2, text3, text4, text5;
    private int text1Count, text2Count, text3Count, text4Count, text5Count;

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

        System.out.println(text1.toString() + " : " + text1Count);
        System.out.println(text2.toString() + " : " + text2Count);
        System.out.println(text3.toString() + " : " + text3Count);
        System.out.println(text4.toString() + " : " + text4Count);
        System.out.println(text5.toString() + " : " + text5Count);
        //String textToBeTranslated = "Computer";
        String languagePair = "en-tr"; //English to French ("<source_language>-<target_language>")
        //Executing the translation function
        Translate(text1, languagePair);
        Translate(text2, languagePair);
        Translate(text3, languagePair);
        Translate(text4, languagePair);
        Translate(text5, languagePair);

    }

    public void Translate(String textToBeTranslated, String languagePair) {
        TranslatorBackgroundTask translatorBackgroundTask = new TranslatorBackgroundTask(getApplicationContext());
        translatorBackgroundTask.execute(textToBeTranslated, languagePair); // Returns the translated text as a String
        //Log.d("Translation Result",translationResult); // Logs the result in Android Monitor
    }

    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {
        //Declare Context
        Context ctx;

        //Set Context
        TranslatorBackgroundTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            //String variables
            String textToBeTranslated = params[0];
            String languagePair = params[1];

            String jsonString;
            String resultString=null;
            StringBuilder jsonStringBuilder = null;

            try {
                //Set up the translation call URL
                String yandexKey = "trnsl.1.1.20171103T125201Z.7a2810b7e8460567.a62e2eacb3f268cac2e88e24e23bbd64f8b29e43";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);

                //Set Http Conncection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                resultString = jsonStringBuilder.toString().trim();
                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[') + 1);
                resultString = resultString.substring(0, resultString.indexOf("]"));
                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"") + 1);
                resultString = resultString.substring(0, resultString.indexOf("\""));

                //Log.d("Translation Result:", resultString);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Translation Result:", result.toString());
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }


}
