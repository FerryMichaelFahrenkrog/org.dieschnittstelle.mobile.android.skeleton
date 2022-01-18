package model;

import java.util.List;

public interface IDataItemCRUDOperations
{
    ToDo createDataItem(ToDo toDo);

    List<ToDo> readAllDataItems();

    ToDo readDataItem(long id);

    boolean updateDataItem(ToDo toDo);

    boolean deleteDataItem(ToDo toDo);

    boolean deleteAllDataItems(boolean remote);

    boolean authenticateUser(User user);
}
