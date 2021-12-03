package org.dieschnittstelle.mobile.android.skeleton;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailviewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private EditText itemNameText;
    private String item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Zustand wiedeherstellen wenn man das Handy dreht

        setContentView(R.layout.activity_detailview);
        itemNameText = findViewById(R.id.itemName);

        item = getIntent().getStringExtra(ARG_ITEM);
        itemNameText.setText(item); //Data Binding
    }
}
