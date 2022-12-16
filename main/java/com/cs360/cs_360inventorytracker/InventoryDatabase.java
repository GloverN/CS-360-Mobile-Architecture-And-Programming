package com.cs360.cs_360inventorytracker;

/*
 * This abstract class sets up the SQLite databases for both the user accounts and the items stored
 * in the inventory.
 */
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Account.class, Item.class}, version = 1)
public abstract class InventoryDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "inventory.db";

    private static InventoryDatabase mInventoryDatabase;

    public static InventoryDatabase getInstance(Context context) {
        if (mInventoryDatabase == null) {
            mInventoryDatabase = Room.databaseBuilder(context, InventoryDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries().build();
        }

        return mInventoryDatabase;
    }

    public abstract AccountDao accountDao();
    public abstract ItemDao itemDao();
}
