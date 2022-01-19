package model;

import java.util.List;

public interface IDataItemCRUDOperations
{
    ToDo createToDo(ToDo toDo);

    List<ToDo> readAllToDos();

    ToDo readToDo(long id);

    boolean updateToDo(ToDo toDo);

    boolean deleteToDo(ToDo toDo);

    boolean deleteAllToDos(boolean remote);

    boolean authenticateUser(User user);
}
