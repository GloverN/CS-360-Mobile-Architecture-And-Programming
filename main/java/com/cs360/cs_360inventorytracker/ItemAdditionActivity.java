package com.cs360.cs_360inventorytracker;

/*
 * This activity handles the main addition of items to the inventory list. It takes the user-entered
 * information and adds it to the item database.
 */
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ItemAdditionActivity extends AppCompatActivity {

    private EditText mItemName;
    private EditText mItemQuant;
    private Button mCreateItemButton;

    private boolean isNameEmpty = true;
    private boolean isQuantEmpty = true;

    private InventoryDatabase mInventoryDb;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_addition);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        mItemName = findViewById(R.id.enterName);
        mItemQuant = findViewById(R.id.enterQuant);
        mCreateItemButton = findViewById(R.id.createItemButton);

        mItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isNameEmpty = mItemName.getText().toString().isEmpty();
                mCreateItemButton.setEnabled(!isNameEmpty && !isQuantEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mItemQuant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isQuantEmpty = mItemQuant.getText().toString().isEmpty();
                mCreateItemButton.setEnabled(!isNameEmpty && !isQuantEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onCreateItemClicked(View view) {
        String name = mItemName.getText().toString();
        int quantity = Integer.parseInt(mItemQuant.getText().toString());

        mItem = new Item(name, quantity);
        mInventoryDb.itemDao().insertItem(mItem);

        finish();
    }
}