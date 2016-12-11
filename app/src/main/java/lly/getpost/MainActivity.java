package lly.getpost;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ScrollView scrollView;
    private List<String> list ;
    private TextView textView;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x00 :
                    textView.setText((String)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        textView = (TextView) findViewById(R.id.textView);

        list = new ArrayList<>();
        list.add("urlconnection_get");
        list.add("urlconnection_post");
        list.add("httpclient_get");
        list.add("httpclient_post");
        listView.setAdapter(new ButtonAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        switch (position){
                            case 0:
                                String get_url = "http://httpbin.org/get?aa=bb&cc=dd";
                                try {
                                    URL url = new URL(get_url);
                                    URLConnection urlConnection = url.openConnection();
                                    urlConnection.setConnectTimeout(5000);
                                    urlConnection.setReadTimeout(5000);

                                    // 设置通用的请求属性
                                    urlConnection.setRequestProperty("accept", "*/*");
                                    urlConnection.setRequestProperty("connection", "Keep-Alive");
                                    urlConnection.setRequestProperty("user-agent",
                                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

                                    urlConnection.connect();
                                    // 获取所有响应头字段
                                    Map<String, List<String>> map = urlConnection.getHeaderFields();
                                    // 遍历所有的响应头字段
                                    for (String key : map.keySet()) {
                                        System.out.println(key + "--->" + map.get(key));
                                    }
                                    BufferedReader bufferedReader = new BufferedReader(
                                            new InputStreamReader(urlConnection.getInputStream()));
                                    String line;
                                    String result = "";
                                    while ((line = bufferedReader.readLine()) != null) {
                                        result += "/n" + line;
                                    }

                                  System.out.println(result);
                                    Message message = handler.obtainMessage();
                                    message.what = 0x00;
                                    message.obj = result;
                                    handler.sendMessage(message);


                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                String post_url = "http://httpbin.org/post";
                                try {
                                    URL url = new URL(post_url);
                                    URLConnection urlConnection = url.openConnection();

                                    urlConnection.setRequestProperty("accept", "*/*");
                                    urlConnection.setRequestProperty("connection", "Keep-Alive");
                                    urlConnection.setRequestProperty("user-agent",
                                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

                                    urlConnection.setDoInput(true);
                                    urlConnection.setDoOutput(true);
                                    PrintStream printStream = new PrintStream(urlConnection.getOutputStream());
                                    printStream.print("11=22");
                                    printStream.print("&");
                                    printStream.print("33=44");
                                    printStream.flush();

                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                                    String line = "";
                                    String results = "";
                                    while((line = bufferedReader.readLine()) != null){
                                        results += "\n" + line;
                                    }
                                    Message message = handler.obtainMessage();
                                    message.what = 0x00;
                                    message.obj = results;
                                    handler.sendMessage(message);


                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case 2:
                                 get_url = "http://httpbin.org/get?aa=bb&cc=dd";

                                    //URL url = new URL(get_url);
                                    HttpClient httpClient = new DefaultHttpClient();
                                    HttpGet httpGet = new HttpGet(get_url);
                                try {
                                    HttpResponse execute = httpClient.execute(httpGet);
                                    InputStream inputStream = execute.getEntity().getContent();
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                                    String line = "";
                                    String results = "";
                                    while((line = bufferedReader.readLine()) != null){
                                        results += "\n" + line;
                                    }
                                    Message message = handler.obtainMessage();
                                    message.what = 0x00;
                                    message.obj = results;
                                    handler.sendMessage(message);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                break;
                            case 3:

                                break;
                        }
                    }
                }.start();

            }
        });
    }

    private class ButtonAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null){
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.button_layout,viewGroup,false);
                viewHolder.textView = (TextView) view.findViewById(R.id.btn1);

                view.setTag(viewHolder);
            }else{
                 viewHolder = (ViewHolder) view.getTag();
            }
            Log.e("xx",i + "," + list.get(i));

            viewHolder.textView.setText(list.get(i));

            return view;
        }

        public final class ViewHolder{
            TextView textView;

        }
    }
}
