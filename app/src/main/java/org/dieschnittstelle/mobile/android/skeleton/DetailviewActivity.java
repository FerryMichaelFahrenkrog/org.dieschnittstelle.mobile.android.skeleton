package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity {

    public static final int ACTION_PICK_CONTACT = 0;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.selectContact){
            selectContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void selectContact(){
        Intent selectContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(selectContactIntent,ACTION_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ACTION_PICK_CONTACT && resultCode == Activity.RESULT_OK)
        {
            Log.i("DetailviewActivity", "onActivityResult(): got data: " + data);
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //1h 15 REST
}
