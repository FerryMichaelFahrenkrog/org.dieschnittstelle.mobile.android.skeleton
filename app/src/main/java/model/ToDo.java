package model;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import impl.RoomLocalDataItemCRUDOperationsImpl;

@Entity                                                                                             // Damit sage ich, dass ich Instanzen der Klasse To-Do in meiner Datenbank ablegen möchte
public class ToDo implements Serializable                                                           // Objekte in Bytes verwandeln, und dann wieder Objekte draus machen, braucht man um Objekte in DB zu speichern
{
    @Expose
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @Expose
    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @Expose
    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description = "";

    @Expose
    @SerializedName("done")
    @ColumnInfo(name = "done")
    private boolean checked;

    @Expose
    @SerializedName("favourite")
    @ColumnInfo(name = "marked")
    private boolean favouriteToDo;

    @Expose
    @SerializedName("expiry")
    @ColumnInfo(name = "finishDate")
    private long finishDateLong = 0;

    @Expose
    @SerializedName("contacts")
    @TypeConverters(RoomLocalDataItemCRUDOperationsImpl.ArrayListToStringDatabaseConverter.class)
    private ArrayList<String> contactIds = new ArrayList<>();

    @SuppressLint("NewApi")
    @Ignore
    private transient LocalDateTime finishDate = LocalDateTime.now();

    @Ignore
    private transient Date localdate = new Date();

    @Ignore
    public ToDo() {
        finishDateLong = System.currentTimeMillis();
    }

    public ToDo(String name) {
        this.name = name;
    }

    @Ignore
    public ToDo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Ignore
    public ToDo(String name, String description, LocalDateTime finishDate) {
        this.name = name;
        this.description = description;
        this.finishDate = finishDate;
    }

    public ToDo(String name, String description, boolean favouriteToDo, LocalDateTime finishDate) {
        this.name = name;
        this.description = description;
        this.favouriteToDo = favouriteToDo;
        this.finishDate = finishDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        Log.i(logtag, "getName()" + name);

        if(name == null)
        {
            return "";
        }

        return name;
    }

    public void setName(String name) {
        Log.i(logtag, "setName()" + name);
        this.name = name;
    }

    public String getDescription() {
        Log.i(logtag, "getDescription" + description);

        return description;
    }

    public void setDescription(String description) {
        Log.i(logtag, "setDescription" + description);
        this.description = description;
    }

    public boolean isChecked() {
        Log.i(logtag, "isChecked" + checked);

        return checked;
    }

    public void setChecked(boolean checked) {
        Log.i(logtag, "isChecked" + checked);
        this.checked = checked;
    }

    public boolean isFavouriteToDo() {
        return favouriteToDo;
    }

    public void setFavouriteToDo(boolean favouriteToDo) {
        this.favouriteToDo = favouriteToDo;
    }

    public long getFinishDateLong() {
        return finishDateLong;
    }

    public void setFinishDateLong(long finishDateLong) {
        this.finishDateLong = finishDateLong;
    }

    public Date getLocaldate() {
        return localdate;
    }

    public void setLocaldate(Date localdate) {
        this.localdate = localdate;
    }

    public ArrayList<String> getContactIds() {
        if(contactIds == null){
            contactIds = new ArrayList<>();
            return contactIds;
        }
        return contactIds;
    }

    public void setContactIds(ArrayList<String> contactIds) {
        this.contactIds = contactIds;
    }

//    public String getContactsString() {
//        return contactsString;
//    }
//
//    public void setContactsString(String contactsString) {
//        this.contactsString = contactsString;
//    }

    @SuppressLint("NewApi")
    public LocalDateTime getFinishDate() {
        this.finishDate = LocalDateTime.ofEpochSecond(this.finishDateLong / 1000, 0, ZoneOffset.UTC);
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                ", favouriteToDo=" + favouriteToDo +
                ", faelligkeitsDatumLong=" + finishDateLong +
                ", contactIds=" + contactIds +
                ", faelligkeitsDatum=" + finishDate +
                ", localdate=" + localdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return id == toDo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected static long ID_GENERATOR = 0;

    public static long nextId(){
        return ++ID_GENERATOR;
    }

    protected static String logtag ="ToDo";
}
