package tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.function.Consumer;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class UpdateToDosTask extends AsyncTask<ToDo, Void, Boolean> {
    //Die Warning irgendwann beheben
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<Boolean> onDoneConsumer;

    public UpdateToDosTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<Boolean> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Boolean doInBackground(ToDo... todos) {
        return crudOperations.updateDataItem(todos[0]);

//        for (ToDo todo : todos) {
//            crudOperations.updateDataItem(todo);
//        }
//        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        onDoneConsumer.accept(result);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
