package cc.denone.com.ratemyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cc.denone.com.apprateprompt.AppRateUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume(){
        super.onResume();
        AppRateUtils.initRateApp(this, 15, 3, 3, "title fuck", "message fuck", "kk", "nn", "maybe");
    }

}
