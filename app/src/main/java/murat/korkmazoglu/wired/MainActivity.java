package murat.korkmazoglu.wired;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog dialog;
    private List<NewsModel> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview);

        new NewsService().execute("https://www.wired.com/feed/");
    }

    class NewsService extends AsyncTask<String, String, List<NewsModel>> {

        @Override
        protected List<NewsModel> doInBackground(String... params) {

            modelList = new ArrayList<NewsModel>();
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                int connectionState = connection.getResponseCode();

                if (connectionState == HttpURLConnection.HTTP_OK) {
                    BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                    publishProgress("Haberler Hazırlanıyor...");
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
                    Document document = documentBuilder.parse(bis);

                    NodeList newsNodeList = document.getElementsByTagName("item");
                    for (int i = 0; i < 4; i++) {
                        Element element = (Element) newsNodeList.item(i);
                        NodeList nodeListTitle = element.getElementsByTagName("title");
                        NodeList nodeListLink = element.getElementsByTagName("link");
                        NodeList nodeListDate = element.getElementsByTagName("pubDate");
                        NodeList nodeListCreator = element.getElementsByTagName("dc:creator");
                        NodeList nodeListDescription = element.getElementsByTagName("description");
                        NodeList nodeListMedia = element.getElementsByTagName("media:thumbnail");

                        String title = nodeListTitle.item(0).getFirstChild().getNodeValue();
                        String link = nodeListLink.item(0).getFirstChild().getNodeValue();
                        String date = nodeListDate.item(0).getFirstChild().getNodeValue();
                        String creator = nodeListCreator.item(0).getFirstChild().getNodeValue();
                        String description = nodeListDescription.item(0).getFirstChild().getNodeValue();
                        String media = nodeListMedia.item(0).getAttributes().getNamedItem("url").getNodeValue();
                        //Log.d("MEDİA",media.toString());

                        NewsModel model = new NewsModel();
                        model.setTitle(title);
                        model.setCreator(creator);
                        model.setLink(link);
                        model.setDate(date);
                        model.setImage(media);

//                        Pattern p = Pattern.compile(".*<img[^>]*src=\"([^\"]*)", Pattern.CASE_INSENSITIVE);
//                        Matcher m = p.matcher(media);
//                        String photoUrl = null;
//
//                        while (m.find()) {
//                            photoUrl = m.group(1);
//                            Bitmap bitmap = null;
//
//                            try {
//                                URL urlResim = new URL(photoUrl.toString().trim());
//                                InputStream is = urlResim.openConnection().getInputStream();
//                                bitmap = BitmapFactory.decodeStream(is);
//
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                            model.setImage(bitmap);
//                        }
                        modelList.add(model);
                        publishProgress("Liste Güncelleniyor...");
                    }

                } else {
                    //Connection nothing
                    Toast.makeText(MainActivity.this, "İnternet Bağlantınızı Kontrol Ediniz", Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();

            }


            return modelList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=ProgressDialog.show(MainActivity.this,"Lütfen Bekleyiniz","İşlem Devam Ediyor",true);
        }

        @Override
        protected void onPostExecute(List<NewsModel> newsModels) {
            super.onPostExecute(newsModels);
            CustomAdapter adapter=new CustomAdapter(MainActivity.this,newsModels);
            listView.setAdapter(adapter);
            dialog.cancel();


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            dialog.setMessage(values[0]);
        }
    }
}
