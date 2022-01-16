package tasks;

import android.os.AsyncTask;

import org.dieschnittstelle.mobile.android.skeleton.ToDoApplication;

import java.util.function.Consumer;

public class CheckWebapiAvailableTask extends AsyncTask<Void, Void, Boolean> {
    private Consumer<Boolean> onDoneConsumer;

    public CheckWebapiAvailableTask(Consumer<Boolean> onDoneConsumer) {
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean remoteVerbindung = ToDoApplication.checkConnectivity();

        return remoteVerbindung;
    }

    @Override
    protected void onPostExecute(Boolean todos) {
        onDoneConsumer.accept(todos);
    }
}
