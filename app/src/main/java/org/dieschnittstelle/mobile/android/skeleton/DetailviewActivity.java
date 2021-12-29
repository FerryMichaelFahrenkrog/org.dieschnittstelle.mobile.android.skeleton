package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

        Log.i("DetailviewActivity", "got contact ids: " + item.getContactIds());
        item.getContactIds().forEach(id -> {
            showContactDetailsForInternal(Long.parseLong(id));
        });

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
            showContactDetails(data.getData());
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("DetailviewActivity", "onRequestPermissionsResult(): permission");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void showContactDetails(Uri contactId){
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if(hasReadContactsPermission != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1 );
            return;
        }

        Cursor cursor = getContentResolver().query(contactId, null, null, null, null);
        if(cursor.moveToFirst()){
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            long internalContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            if(!item.getContactIds().contains(String.valueOf(contactId))){
                item.getContactIds().add(String.valueOf(internalContactId));
            }


            Log.i("DetailviewActivity", "got contact with name " + contactName + " and internal id: " + internalContactId);

            showContactDetailsForInternal(internalContactId);
        }
        else{
            Log.i("DetailviewActivity", "no contact found");
        }
    }

    //UI BNEFÜLLEN
    public void showContactDetailsForInternal(long internalId) {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(internalId)}, null);
        if(cursor.moveToNext()){
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("DetailviewActivity", "found display name for internal id " + internalId + ": " + displayName);
        }else{
            Log.i("DetailviewActivity", "no contacts fount for internal id " + internalId);
        }

         cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(internalId),}, null);
        while(cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneNumberType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            Log.i("DetailviewActivity", "found number of type " + number + ", of type " + phoneNumberType + ", mobile: " + (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE));
        }
    }
}
