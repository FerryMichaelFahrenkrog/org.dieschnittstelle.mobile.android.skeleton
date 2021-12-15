package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.R;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity
{
    //Bedienelemente
    private ViewGroup listView;
    private FloatingActionButton addNewItemButton;

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        addNewItemButton = findViewById(R.id.addNewItemButton);

        for(int i=0; i<listView.getChildCount(); i++){
            TextView currentChild = (TextView) listView.getChildAt(i);
            currentChild.setOnClickListener(v -> {
                onItemSelected(currentChild.getText().toString());
            });
        }
        addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());
    }

    protected void onItemSelected(String itemName){
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, itemName); // in Arg Item das zweite reinpacken
        this.startActivity(detailviewIntent);
    }

    protected void onItemCreationRequested(){
        Intent detailviewForCreateIntent = new Intent(this, DetailviewActivity.class);
        startActivityForResult(detailviewForCreateIntent, CALL_DETAILVIEW_FOR_CREATE);
    }

    //1h 17min DRIN LASSEN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CALL_DETAILVIEW_FOR_CREATE){
            if(resultCode == Activity.RESULT_OK)    // Sagt aus ob man einfach nur zurückgegangen ist oder was durchgeführt hat
            {
                onNewItemCreated(data.getStringExtra(DetailviewActivity.ARG_ITEM));  //1h 28 min!!!!!!!!!!!!!
            }
            else{
                showFeedbackMessage("Returning from detailview with " + resultCode);
            }
        }
    }

    protected void showFeedbackMessage(String msg){
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void onNewItemCreated(String itemName){
//        showFeedbackMessage("created new item " + itemName);
        TextView newItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_main_listitem, null);
        newItemView.setText(itemName);
        listView.addView(newItemView);
    }
}
