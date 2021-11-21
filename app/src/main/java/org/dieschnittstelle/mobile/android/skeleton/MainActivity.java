package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.dieschnittstelle.mobile.android.skeleton.R;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
{
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setOnClickListener((v) -> {
            Toast.makeText(this, R.string.welcome_toast_text, Toast.LENGTH_SHORT).show();    //1h 20min
        });
        //R verknüpft Java + XML!
    }
}
