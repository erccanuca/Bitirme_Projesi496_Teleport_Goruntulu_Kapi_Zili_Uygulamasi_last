package myproject.example.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView disclaimer = (TextView) findViewById(R.id.disclaimer);
        disclaimer.setText(getText(R.string.disclaimer_text).toString());
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) finish();
    }
}
