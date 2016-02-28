package com.example.fumiyaseki.yodozon;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private String query = "";
    private Button button;
    private ListView listView;
    private String mode = "yodobashi";


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);

        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                query = editText.getText().toString();
                String yodobashiUrl = String.format("http://www.yodobashi.com/ec/category/index.html?cate=&word=%s&gint=\"\"", query);
                getHtmlSource(yodobashiUrl, mode);
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    button.performClick();
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("デバッグ", "クリックされた");
                ListView listView = (ListView) parent;
                final Commodity c = (Commodity) listView.getItemAtPosition(position);
                String regex = "\\[(.+)?\\]";
                c.name = c.name.replaceAll(regex, "");
                regex = " ";
                c.name = c.name.replaceAll(regex, "+");
                String amazonUrl = String.format("http://www.amazon.co.jp/s/ref=sr_gnr_fkmr0?keywords=%s", c.name);
                String yodobashiUrl = String.format("http://www.yodobashi.com/ec/category/index.html?cate=&word=%s&gint=\"\"", c.name);
                if (mode == "yodobashi") {
                    mode = "amazon";
                    getHtmlSource(amazonUrl, mode);
                } else if (mode == "amazon"){
                    mode = "yodobashi";
                    getHtmlSource(yodobashiUrl, mode);
                }
            }
        });
    }

    private void getHtmlSource(String urlString, String mode){
        DownloadTask task = new DownloadTask(urlString, listView, mode, this);
        task.execute("start");
    }

}
