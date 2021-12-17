package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.R;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.ToDo;

public class MainActivity extends AppCompatActivity {
    private static String logtag = "MainActivity";

    //Bedienelemente
    private ListView listView;
    private List<ToDo> items = Arrays.asList("lorem", "ipsum", "dolor", "sit", "amet", "adipiscing", "elit", "larem", "totkopf", "druggy")
            .stream()
            .map(v -> {
                    ToDo itemobj = new ToDo(v);
                    itemobj.setId(ToDo.nextId());
                    return itemobj;
            })
            .collect(Collectors.toList()); //59 MIN

    private ArrayAdapter<ToDo> listViewAdapter;//Selber Typ wie oben



    private FloatingActionButton addNewItemButton;

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;


    private class DataItemsAdapter extends ArrayAdapter<ToDo>{ //1h.33min
        private int layoutResource;

        //AdapterViews sind dafür da ListViews mit Teilansichten auszustatten.

        public DataItemsAdapter(@NonNull Context context, int resource, @NonNull List<ToDo> objects) { // für uns angepasst!
            super(context, resource, objects);
            layoutResource = resource;
        }

        //Muss man überschreiben, wenn man mehrere Dinger haben will
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ToDo currentItem = getItem(position);   // Erstmal richtige Stelle auslesen
            View currentView = getLayoutInflater().inflate(layoutResource, null);    // Dann richtiges layout laden für diese Stelle!

            TextView itemNameText = currentView.findViewById(R.id.itemName);
            itemNameText.setText(currentItem.getName());
            CheckBox itemChecked = currentView.findViewById(R.id.itemChecked);
            itemChecked.setChecked(currentItem.isChecked());

            return currentView;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listViewAdapter = new DataItemsAdapter(this, R.layout.activity_main_listitem, items); // Er hat also die Daten und die ART DER DARSTELLUNG ;)
        listView.setAdapter(listViewAdapter);

        addNewItemButton = findViewById(R.id.addNewItemButton);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDo selectedItem = listViewAdapter.getItem(position);
                onItemSelected(selectedItem);
            }
        });

        addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());
    }

    protected void onItemSelected(ToDo itemName){
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, itemName); // in Arg Item das zweite reinpacken //Check mal Seriziable
        this.startActivityForResult(detailviewIntent, CALL_DETAILVIEW_FOR_EDIT);
    }

    protected void onItemCreationRequested(){
        Intent detailviewForCreateIntent = new Intent(this, DetailviewActivity.class);
        startActivityForResult(detailviewForCreateIntent, CALL_DETAILVIEW_FOR_CREATE);
    }

    //1h 17min DRIN LASSEN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_DETAILVIEW_FOR_CREATE) {
            if (resultCode == Activity.RESULT_OK) {   // Sagt aus ob man einfach nur zurückgegangen ist oder was durchgeführt hat{
                onNewItemCreated((ToDo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM));  //1h 28 min!!!!!!!!!!!!!
        } else {
            showFeedbackMessage("Returning from detailview for create with " + resultCode);
        }
    }
            else if(requestCode == CALL_DETAILVIEW_FOR_EDIT){
                if(resultCode == Activity.RESULT_OK){
                    ToDo editedItem = (ToDo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM);
                    showFeedbackMessage("Got updated item " + editedItem.getName());
                    onItemEdited(editedItem);
                }
                else{
                    showFeedbackMessage("Returning from detailview for edit with: " + resultCode);
                }
            }
            else{
                showFeedbackMessage("Returning from detailview with " + resultCode);
            }
        }

    protected void showFeedbackMessage(String msg){
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void onNewItemCreated(ToDo item){
//        showFeedbackMessage("created new item " + itemName);
//        TextView newItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_main_listitem, null);
//        newItemView.setText(itemName);
//        listView.addView(newItemView);
//        Log.i(logtag, "list ist now: " + items);
        item.setId(ToDo.nextId());
        items.add(item);
        listViewAdapter.notifyDataSetChanged();
        listView.setSelection(listViewAdapter.getPosition(item));

    }

    protected void onItemEdited(ToDo editedItem)
    {
        int pos = items.indexOf(editedItem);
        Log.i(logtag, "got position: " + pos);

        items.remove(pos);
        items.add(pos, editedItem);
        listViewAdapter.notifyDataSetChanged();
        listView.setSelection(pos);

    }
}
