package tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.function.Consumer;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class DeleteTodosTask extends AsyncTask<ToDo, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<Boolean> onDoneConsumer;

    public DeleteTodosTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<Boolean> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected Boolean doInBackground(ToDo... todos) {

        for (ToDo todo : todos) {
            crudOperations.deleteToDo(todo);
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean aBool) {
        onDoneConsumer.accept(aBool);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
