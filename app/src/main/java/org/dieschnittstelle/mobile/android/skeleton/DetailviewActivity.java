package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";

    private ToDo item;
    private ActivityDetailviewBinding dataBindingHandle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Zustand wiedeherstellen wenn man das Handy dreht

        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview); // braucht man bei DB

        item = (ToDo)getIntent().getSerializableExtra(ARG_ITEM);

        if(item == null){
            item = new ToDo(); // Leeres DataItem
        }

        this.dataBindingHandle.setController(this);
    }

    public void onSaveItem(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    public ToDo getItem() {
        return item;
    }

    public void setItem(ToDo item) {
        this.item = item;
    }
}
