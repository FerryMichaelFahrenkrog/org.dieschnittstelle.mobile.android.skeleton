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
import java.util.Arrays;
import java.util.Comparator;
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
    @Ignore
    private ArrayList<String> contacts = new ArrayList<>();

    //An dieser Stelle nutzt er private transient String concactsString; wegen der persistierung
    @ColumnInfo(name = "contacts")
    private transient String contactsString = "";

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

    public ArrayList<String> getContacts() {
        if(contacts == null){
            contacts = new ArrayList<>();
            return contacts;
        }
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    public String getContactsString() {
        return contactsString;
    }

    public void setContactsString(String contactsString) {
        this.contactsString = contactsString;
    }

    private void updateListFromString() {
        if (contactsString != null && !contactsString.equals("")) {
            this.contacts = new ArrayList<>(Arrays.asList(this.contactsString.split("-;-")));
        }
    }

    private void updateStringFromList() {
        if (this.contacts == null) {
            this.contacts = new ArrayList<>();
        }

        String newContactsString = "";

        for (int i = 0; i < this.contacts.size(); i++) {
            newContactsString += this.contacts.get(i);

            if (i < this.contacts.size() - 1) {
                newContactsString += "-;-";
            }
        }

        this.contactsString = newContactsString;
    }

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
                ", contactIds=" + contacts +
                ", faelligkeitsDatum=" + finishDate +
                ", localdate=" + localdate +
                '}';
    }

    @SuppressLint("NewApi")
    public static Comparator<ToDo> importanceBeforeDate = (objekt_eins, objekt_zwei) -> {
        int o1Greater = 1;
        int o1Smaller = -1;
        int equal = 0;

        //Wenn Objekt 1 Checked ist und Objekt 2 nicht
        if (objekt_eins.isChecked() && !objekt_zwei.isChecked()) {
            // Dann ist Objekt 1 größer
            return o1Greater;
            //Wenn Objekt 1 nicht checked ist, Objekt 2 aber schon
        } else if (!objekt_eins.isChecked() && objekt_zwei.isChecked()) {
            //Dann ist Objekt 1 kleiner
            return o1Smaller;
        } else {
            //
            if (objekt_eins.isChecked() && !objekt_zwei.isChecked()) {
                return o1Smaller;
            } else if (!objekt_eins.isChecked() && objekt_zwei.isChecked()) {
                return o1Greater;
            } else {
                return objekt_eins.getFinishDate().compareTo(objekt_zwei.getFinishDate());
            }
        }
    };

    @SuppressLint("NewApi")
    public static Comparator<ToDo> dateBeforeImportance = (o1, o2) -> {
        int o1Greater = 1;
        int o1Smaller = -1;
        int equal = 0;

        if (o1.isChecked() && !o2.isChecked()) {
            return o1Greater;
        } else if (!o1.isChecked() && o2.isChecked()) {
            return o1Smaller;
        } else {
            int dateComparison = o1.getFinishDate().compareTo(o2.getFinishDate());

            if (dateComparison > 0) {
                return o1Greater;
            } else if (dateComparison < 0) {
                return o1Smaller;
            } else {
                if (o1.isChecked() && !o2.isChecked()) {
                    return o1Smaller;
                } else if (!o1.isChecked() && o2.isChecked()) {
                    return o1Greater;
                } else {
                    return equal;
                }
            }
        }
    };

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
