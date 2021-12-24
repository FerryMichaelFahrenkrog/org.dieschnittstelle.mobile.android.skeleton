package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;
import android.widget.Toast;

import impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import impl.RoomLocalDataItemCRUDOperationsImpl;
import model.IDataItemCRUDOperations;

public class ToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Application started...", Toast.LENGTH_SHORT).show();
    }

    public IDataItemCRUDOperations getCrudOperations()
    {
//        return new RoomLocalDataItemCRUDOperationsImpl(this);
        return new RetrofitRemoteDataItemCRUDOperationsImpl();
    }
}
