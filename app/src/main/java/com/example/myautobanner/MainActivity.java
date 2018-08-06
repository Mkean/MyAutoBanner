package com.example.myautobanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AuToBanner auto_banner;
    private ArrayList<Integer> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auto_banner = findViewById(R.id.auto_banner);
        urls = new ArrayList<>();
        if (urls.size() == 0) {
            urls.add(R.drawable.picture1);
            urls.add(R.drawable.picture2);
            urls.add(R.drawable.picture3);
            urls.add(R.drawable.picture4);
        }
        Log.e("TAG", urls.size() + "");
        //解耦
        auto_banner.loadData(urls).display();//构建者模式返回对象本身
        auto_banner.setBannerClicklistener(new AuToBanner.BannerClicklistener() {
            @Override
            public void onClickListener(int pos) {
                Toast.makeText(MainActivity.this, "" + pos, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
