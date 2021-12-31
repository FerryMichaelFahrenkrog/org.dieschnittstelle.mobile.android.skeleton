package model;

import java.util.List;

public interface IDataItemCRUDOperations {
    public ToDo createDataItem(ToDo toDo);

    public List<ToDo> readAllDataItems();

    public ToDo readDataItem(long id);

    public ToDo updateDataItem(ToDo toDo);

    public boolean deleteDataItem(long id);

    public boolean deleteAllDataItems(boolean remote);

}
