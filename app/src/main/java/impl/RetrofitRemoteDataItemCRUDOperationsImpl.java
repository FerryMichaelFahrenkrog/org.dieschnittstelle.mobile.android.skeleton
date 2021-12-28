package impl;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.IDataItemCRUDOperations;
import model.ToDo;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitRemoteDataItemCRUDOperationsImpl implements IDataItemCRUDOperations
{
    public static interface ToDoWebAPI{
        //Web API Dokument

        @POST("/api/todos")
        public Call<ToDo> createToDo(@Body ToDo toDo);

        @GET("/api/todos")
        public Call<List<ToDo>> readAllToDos();

        @GET("/api/todos/{id}")
        public Call<ToDo> readToDo(@Path("id")long id);

        @PUT("/api/todos/{id}")
        public Call<ToDo> updateToDo(@Path("id")long id,@Body ToDo toDo);

        @DELETE("/api/todos/{id}")
        public Call<Boolean> deleteToDo(@Path("id") long id);

        @DELETE("/api/todos")
        public Call<Boolean> deleteAllToDos();
        //1h 24 min rest, ab 30 min Blick auf alles
    }

    private ToDoWebAPI webAPI;

    public RetrofitRemoteDataItemCRUDOperationsImpl()
    {
        Retrofit apiBase = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webAPI = apiBase.create(ToDoWebAPI.class);
    }

    @Override
    public ToDo createDataItem(ToDo toDo) {
        try {
            return webAPI.createToDo(toDo).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ToDo> readAllDataItems() {
        try {
            return webAPI.readAllToDos().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public ToDo readDataItem(long id) {
        try {
            return webAPI.readToDo(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ToDo updateDataItem(ToDo toDo) {
        try {
            return webAPI.updateToDo(toDo.getId(), toDo).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteDataItem(long id) {
        try {
            return webAPI.deleteToDo(id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}