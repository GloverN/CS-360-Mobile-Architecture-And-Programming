package com.cs360.cs_360inventorytracker;
 /*
  * This interface is an intermediary between the SQLite database for the items and the rest of the
  * code. This makes the code for accessing and editing the SQLite database much easier to manage.
  */
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM items WHERE name = :name")
    public Item getItem(String name);

    @Query("SELECT * FROM items ORDER BY name")
    public List<Item> getItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertItem(Item item);

    @Update
    public void updateItem(Item item);

    @Delete
    public void deleteItem(Item item);
}
