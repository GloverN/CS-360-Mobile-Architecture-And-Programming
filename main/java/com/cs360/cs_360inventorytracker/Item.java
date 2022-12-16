package com.cs360.cs_360inventorytracker;

/*
 * This object represents each inventory item. It's the object used in the item database to store
 * the item information.
 */
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "quantity")
    private int mQuantity;

    public Item() {
        this.mName = "New Item";
        this.mQuantity = 0;
    }

    public Item(String name) {
        this.mName = name;
        this.mQuantity = 0;
    }

    public Item(String name, int quantity) {
        this.mName = name;
        this.mQuantity = quantity;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }
}
