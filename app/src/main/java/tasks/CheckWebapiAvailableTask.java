package tasks;

import android.os.AsyncTask;

import java.util.function.Consumer;

public class CheckWebapiAvailableTask extends AsyncTask<Void, Void, Boolean> {
    private Consumer<Boolean> onDoneConsumer;

    public CheckWebapiAvailableTask(Consumer<Boolean> onDoneConsumer) {
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
//        return SyncTodoCRUDOperationsImpl.isHostAvailable("10.0.2.2", 8080, 1000);
        return null;
    }

    @Override
    protected void onPostExecute(Boolean todos) {
        onDoneConsumer.accept(todos);
    }
}
