package tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.function.Consumer;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class DeleteAllToDosTask extends AsyncTask<Void, Void, Boolean> {
    //Die Warning irgendwann beheben
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<Boolean> onDoneConsumer;

    public DeleteAllToDosTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<Boolean> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }


    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        for (ToDo todo : crudOperations.readAllDataItems()) {
            crudOperations.deleteDataItem(todo.getId());
        }
        return true;
    }



    @Override
    protected void onPostExecute(Boolean aBool) {
        onDoneConsumer.accept(aBool);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
