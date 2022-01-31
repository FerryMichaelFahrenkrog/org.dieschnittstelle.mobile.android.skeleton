package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import impl.RoomLocalDataItemCRUDOperationsImpl;
import impl.SyncedDataItemCRUDOperationsImpl;
import model.IDataItemCRUDOperations;

public class ToDoApplication extends Application {
    protected static String logtag = "ToDoApplication";
    private IDataItemCRUDOperations crudOperations;
    private boolean serverAvailable;

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Checking connectivity", Toast.LENGTH_SHORT).show();

        Future<Boolean> connectivityFuture = checkConnectivityAsync();

        try{
            if(connectivityFuture.get())
            {
                Log.i(logtag, "connectivity successful");
                Toast.makeText(this, "Backend accessible, will use the remote access.", Toast.LENGTH_SHORT).show();
                crudOperations = new SyncedDataItemCRUDOperationsImpl(new RoomLocalDataItemCRUDOperationsImpl(this), new RetrofitRemoteDataItemCRUDOperationsImpl());
                serverAvailable = true;
            }
            else{
                Log.i(logtag, "connectivity failed");
                Toast.makeText(this, "Backend not accessible, will use local access only.", Toast.LENGTH_SHORT).show();
                crudOperations = new RoomLocalDataItemCRUDOperationsImpl(this);
            }
        }catch(Exception e){
            Log.e(logtag, "onCreate(): Got exception: " + e,e);
            Toast.makeText(this, "Backend not accessible, got exception: " + e + ", will use local access", Toast.LENGTH_SHORT).show();
            crudOperations = new RoomLocalDataItemCRUDOperationsImpl(this);
        }
    }

    public IDataItemCRUDOperations getCrudOperations() {
        return crudOperations;
    }

    public Future<Boolean> checkConnectivityAsync(){
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        new Thread(() -> {
            boolean connectionAvailable = checkConnectivity();
            future.complete(connectionAvailable);
        }).start();

        return future; //2h 4 rest
    }

    public static boolean checkConnectivity() {
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            conn.getInputStream();
            Log.e(logtag, "checkConnectivity(): connection successful ");
            return true;
        } catch (Exception e) {
            Log.e(logtag, "checkConnectivity(): Got exception: " + e,e);
            return false;
        } finally{
            if(conn != null){
                conn.disconnect();
            }
        }
    }
}
