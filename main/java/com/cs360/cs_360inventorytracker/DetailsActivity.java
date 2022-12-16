package com.cs360.cs_360inventorytracker;

/*
 * This activity handles the editing and updating of items already in the inventory.
 * If only the item's quantity is changed, the code pulls the new value and updates the item in the database.
 * If the name or the name and quantity are changed, the activity removes the current item from the database
 * and inserts a new one that fits the user input.
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_NAME = "itemName";

    private EditText mChangeName;
    private EditText mChangeQuant;

    private String oldName;

    private Item mItem;
    private InventoryDatabase mInventoryDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        oldName = getIntent().getStringExtra(EXTRA_ITEM_NAME);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());
        mItem = mInventoryDb.itemDao().getItem(oldName);

        mChangeName = findViewById(R.id.newName);
        mChangeQuant = findViewById(R.id.newQuantity);
        mChangeName.setText(oldName);
        mChangeQuant.setText(String.valueOf(mItem.getQuantity()));
    }

    public void onSubmitChangeClicked(View view) {
        String newName = mChangeName.getText().toString();
        int quantity = Integer.parseInt(mChangeQuant.getText().toString());

        if (!newName.equals(oldName)) {
            mInventoryDb.itemDao().deleteItem(mItem);
            mItem = new Item(newName, quantity);
            mInventoryDb.itemDao().insertItem(mItem);
        }
        else {
            mItem.setQuantity(quantity);
            mInventoryDb.itemDao().updateItem(mItem);
        }

        finish();
    }
}