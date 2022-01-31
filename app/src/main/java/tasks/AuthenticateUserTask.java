package tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.function.Consumer;

import model.IDataItemCRUDOperations;
import model.User;

public class AuthenticateUserTask extends AsyncTask<User, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<Boolean> onDoneConsumer;

    public AuthenticateUserTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<Boolean> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected Boolean doInBackground(User... users) {
        return crudOperations.authenticateUser(users[0]);
    }

    @Override
    protected void onPostExecute(Boolean todos) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onDoneConsumer.accept(todos);
        progressBar.setVisibility(View.INVISIBLE);
    }
}