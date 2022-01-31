package impl;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.IDataItemCRUDOperations;
import model.ToDo;
import model.User;

public class RoomLocalDataItemCRUDOperationsImpl implements IDataItemCRUDOperations
{
    @Database(entities = {ToDo.class}, version = 12)
    public static abstract class RoomToDoDatabase extends RoomDatabase {
        public abstract RoomDataItemCRUDAccess getDao();
    }

    @Dao
    public static interface RoomDataItemCRUDAccess {
        @Insert
        long createItem(ToDo toDo); //TODO als Name!

        @Query("select * from todo")
        List<ToDo> readAllItems();

        @Delete
        void delete(ToDo todo);

        @Query("select * from todo where id = (:id)")
        ToDo readItem(Long id);

        @Update
        int updateItem(ToDo toDo);
    }

    private RoomDataItemCRUDAccess roomAccessor;

    public RoomLocalDataItemCRUDOperationsImpl(Context databaseOwner) {
        RoomToDoDatabase db = Room.databaseBuilder(databaseOwner.getApplicationContext(),
                RoomToDoDatabase.class,
                "dataitems-database-mad21").build();

        roomAccessor = db.getDao();
    }

    @Override
    public ToDo createToDo(ToDo toDo) {
        long newID = roomAccessor.createItem(toDo);
        toDo.setId(newID);

        return toDo;
    }

    @Override
    public List<ToDo> readAllToDos() {
        return roomAccessor.readAllItems();
    }

    @Override
    public ToDo readToDo(long id) {
        return null;
    }

    @Override
    public boolean updateToDo(ToDo toDo) {
        if(roomAccessor.updateItem(toDo) > 0 ){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteToDo(ToDo toDo) {
        roomAccessor.delete(toDo);
        return true;
    }

    @Override
    public boolean deleteAllToDos(boolean remote) {
        if(remote){
            return false;
        }else{
            return false;
        }
    }

    @Override
    public boolean authenticateUser(User user) {
        return true;
    }

    public static class ArrayListToStringDatabaseConverter {

        @TypeConverter
        public static ArrayList<String> fromString(String value) {
            if(value == null || value.length() == 0){
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(value.split(",")));
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<String> value) {
            if(value == null){
                return "";
            }
            return value
                    .stream()
                    .collect(Collectors.joining(","));
        }
    }
}
