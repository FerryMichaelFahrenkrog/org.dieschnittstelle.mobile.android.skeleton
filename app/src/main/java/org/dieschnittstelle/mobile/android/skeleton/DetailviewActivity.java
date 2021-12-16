package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";
    private EditText itemNameText;
    private EditText itemDescription;
    private ToDo item;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Zustand wiedeherstellen wenn man das Handy dreht

        setContentView(R.layout.activity_detailview);

        //1. Bedien- und Anzeigeelemente auslesen
        itemNameText = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        saveButton = findViewById(R.id.saveButton);     // evtl oben refactoren?

        //2. Bedienelemente bedienbar machen
        saveButton.setOnClickListener(v -> onSaveItem());

        //3. Ansicht mit Daten befüllen
        item = (ToDo)getIntent().getSerializableExtra(ARG_ITEM);

        if(item != null){
            itemNameText.setText(item.getName()); //Data Binding
            itemDescription.setText(item.getDescription()); //Data Binding
        }
        else{
            item = new ToDo(); // Leeres DataItem
        }
    }

    protected void onSaveItem(){
        item.setName(itemNameText.getText().toString()); //toString testen?
        item.setDescription(itemNameText.getText().toString()); //toString testen?

        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);

        finish();

    }
}
