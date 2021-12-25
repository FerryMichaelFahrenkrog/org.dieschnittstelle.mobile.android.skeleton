package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainListitemBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import impl.RoomLocalDataItemCRUDOperationsImpl;
import impl.ThreadedDataItemCRUDOperationsAsyncImpl;
import model.IDataItemCRUDOperations;
import model.IDataItemCRUDOperationsAsync;
import model.ToDo;

public class MainActivity extends AppCompatActivity {
    private static String logtag = "MainActivity";

    //Bedienelemente
    private ListView listView;
    private List<ToDo> items = new ArrayList<>();

    private ArrayAdapter<ToDo> listViewAdapter;//Selber Typ wie oben

    private ProgressBar progressBar;

    private FloatingActionButton addNewItemButton;

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;

    private IDataItemCRUDOperationsAsync crudOperations;


    private class DataItemsAdapter extends ArrayAdapter<ToDo> { //1h.33min
        private int layoutResource;

        //AdapterViews sind dafür da ListViews mit Teilansichten auszustatten.

        public DataItemsAdapter(@NonNull Context context, int resource, @NonNull List<ToDo> objects) { // für uns angepasst!
            super(context, resource, objects);
            layoutResource = resource;
        }

        //Muss man überschreiben, wenn man mehrere Dinger haben will
        @NonNull
        @Override
        public View getView(int position, @Nullable View recycleableItemView, @NonNull ViewGroup parent) {
            // HIER PAUSE 1.32h REST!! 19.5!!
            Log.i(logtag, "getView(): for position " + position + " , and recycleableItemView: " + recycleableItemView);

            View itemView = null;
            ToDo currentItem = getItem(position);   // Erstmal richtige Stelle auslesen

            if (recycleableItemView != null) {
                TextView textView = (TextView) recycleableItemView.findViewById(R.id.itemName);
                if (textView != null) {
                    Log.i(logtag, "getView(): itemName in convertView: " + textView);
                    //54 MIN RRRRRRRRRRRRRRRRRRRREST
                }
                itemView = recycleableItemView;
                ActivityMainListitemBinding recycleBinding = (ActivityMainListitemBinding) itemView.getTag();
                recycleBinding.setItem(currentItem);

            } else {
                ActivityMainListitemBinding currentBinding =
                        DataBindingUtil.inflate(getLayoutInflater(),
                                this.layoutResource,
                                null,
                                false);

                currentBinding.setItem(currentItem);
                currentBinding.setController(MainActivity.this); // Innere Klasse in der Main Activity :)

                itemView = currentBinding.getRoot();
                itemView.setTag(currentBinding);
            }
            return itemView;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //1. Access view elements
        listView = findViewById(R.id.listView);
        listViewAdapter = new DataItemsAdapter(this, R.layout.activity_main_listitem, items); // Er hat also die Daten und die ART DER DARSTELLUNG ;)
        listView.setAdapter(listViewAdapter);
        progressBar = findViewById(R.id.progressBar);

        addNewItemButton = findViewById(R.id.addNewItemButton);

        //2- Prepare Elements 4 Interaction
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDo selectedItem = listViewAdapter.getItem(position);
                onItemSelected(selectedItem);
            }
        });

        addNewItemButton.setOnClickListener(v -> this.onItemCreationRequested());

        // 3. Load data into view
        IDataItemCRUDOperations crudExecutor = ((ToDoApplication)this.getApplication()).getCrudOperations();
        crudOperations = new ThreadedDataItemCRUDOperationsAsyncImpl(crudExecutor, this, progressBar);
//        listViewAdapter.addAll(readAllDataItems());
        crudOperations.readAllDataItems(items -> listViewAdapter.addAll(items)); //VK 19.5
    }

    protected void onItemSelected(ToDo itemName) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, itemName); // in Arg Item das zweite reinpacken //Check mal Seriziable
        this.startActivityForResult(detailviewIntent, CALL_DETAILVIEW_FOR_EDIT);
    }

    protected void onItemCreationRequested() {
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
        } else if (requestCode == CALL_DETAILVIEW_FOR_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                ToDo editedItem = (ToDo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM);
                showFeedbackMessage("Got updated item " + editedItem.getName());
                onItemEdited(editedItem);
            } else {
                showFeedbackMessage("Returning from detailview for edit with: " + resultCode);
            }
        } else {
            showFeedbackMessage("Returning from detailview with " + resultCode);
        }
    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }

    protected void onNewItemCreated(ToDo item) {
//        showFeedbackMessage("created new item " + itemName);
//        TextView newItemView = (TextView) getLayoutInflater().inflate(R.layout.activity_main_listitem, null);
//        newItemView.setText(itemName);
//        listView.addView(newItemView);
//        Log.i(logtag, "list ist now: " + items);

        crudOperations.createDataItem(item, created -> {
//            item.setId(ToDo.nextId());
            items.add(created);
            listViewAdapter.notifyDataSetChanged();
            listView.setSelection(listViewAdapter.getPosition(created));
        });
    }

    protected void onItemEdited(ToDo editedItem) {
        crudOperations.updateDataItem(editedItem, updated -> {
            int pos = items.indexOf(updated);
//            Log.i(logtag, "got position: " + pos);

            items.remove(pos);
            items.add(pos, updated);
            listViewAdapter.notifyDataSetChanged();
            listView.setSelection(pos);
        });


    }

    public void onCheckedChangedInListView(ToDo toDo)
        {
        crudOperations.updateDataItem(toDo, updated -> {
        showFeedbackMessage("Checked changed to " + updated.isChecked() + " for " + updated.getName());
        });
    }

    protected void readAllDataItems(Consumer<List<ToDo>> onRead) {

    }
}
