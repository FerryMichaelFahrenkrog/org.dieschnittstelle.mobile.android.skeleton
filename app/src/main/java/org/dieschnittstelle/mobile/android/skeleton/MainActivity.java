package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.R;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity
{
    private ViewGroup listView;     // Allgemeine Gruppe 32:32Min

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        for(int i=0; i<listView.getChildCount(); i++){
            TextView currentChild = (TextView) listView.getChildAt(i);
            currentChild.setOnClickListener(v -> {
                onItemSelected(currentChild.getText().toString());
            });
        }
    }

    protected void onItemSelected(String itemName){
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, itemName); // in Arg Item das zweite reinpacken
        this.startActivity(detailviewIntent);
    }
//
//    protected void showFeedbackMessage(String msg){
//        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_INDEFINITE).show();
//    }
}
