package com.cs360.cs_360inventorytracker;

/*
 * This activity handles the main events of the inventory screen and the zero quantity notifications.
 * It handles the addition of new items, the request for SMS permissions and hosts the fragment that
 * shows the inventory contents. The way the activity handles the notifications is by creating a
 * separate thread that's constantly the items in the database and if any of them have a quantity of
 * zero, it sends an SMS notification.
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InventoryActivity extends AppCompatActivity implements InventoryFragment.OnItemSelectedListener {

    private final int REQUEST_SEND_CODE = 0;

    private ImageButton mSmsNotifsButton;
    private Thread manageNotifs;

    private Fragment fragment;

    private InventoryDatabase mInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        mInventory = InventoryDatabase.getInstance(getApplicationContext());

        mSmsNotifsButton = findViewById(R.id.smsNotifsButton);

        //Creates the thread that constantly checks the item database
        manageNotifs = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!sendNotifs()) {
                    //If the app doesn't have permission, the thread slows down and checks every 5 seconds
                    try {
                        manageNotifs.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //Starts the constant database check loop
                sendNotifs();
            }
        });
        manageNotifs.start();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.invenFragmentContainer);

        if (fragment == null) {
            fragment = new InventoryFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.invenFragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        fragment.onResume();
    }

    @Override
    public void onItemSelected(String itemName) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_ITEM_NAME, itemName);
        startActivity(intent);
    }

    public void onAddItemClicked(View view) {
        Intent intent = new Intent(this, ItemAdditionActivity.class);
        startActivity(intent);
    }

    public void onSmsNotifsClicked(View view) {
        hasSmsPermissions();
        sendNotifs();
    }

    private boolean hasSmsPermissions() {
        String smsPermission = Manifest.permission.SEND_SMS;
        if (ContextCompat.checkSelfPermission(this, smsPermission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, smsPermission)) {
                showPermissionRationaleDialog();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {smsPermission}, REQUEST_SEND_CODE);
            }
            return false;
        }
        return true;
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.smsPermission)
                .setMessage(R.string.smsMessage)
                .setPositiveButton(R.string.smsAllow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String smsPermission = Manifest.permission.SEND_SMS;
                        ActivityCompat.requestPermissions(InventoryActivity.this,
                                new String[] {smsPermission}, REQUEST_SEND_CODE);
                    }
                })
                .setNegativeButton(R.string.smsDeny, null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_SEND_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSmsNotifsButton.setClickable(false);
                    mSmsNotifsButton.setImageResource(R.drawable.sms_notifs_on);
                    EditText pNumber = new EditText(this);
                    pNumber.setHint(R.string.phoneNumberHint);
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.phoneNumber)
                            .setMessage(R.string.phoneNumberMessage)
                            .setView(pNumber)
                            .setPositiveButton(R.string.phoneNumberOk, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String number = pNumber.getText().toString();
                                    writePhoneNumber(number);
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
    }

    private void writePhoneNumber(String number) {
        try {
            FileOutputStream oStream = openFileOutput("phonenumber", Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(oStream);
            writer.println(number);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String readPhoneNumber() {
        try {
            FileInputStream iStream = openFileInput("phonenumber");
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
            return reader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sendNotifs() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        String number = readPhoneNumber();

        mSmsNotifsButton.setClickable(false);
        mSmsNotifsButton.setImageResource(R.drawable.sms_notifs_on);

        /*
         * Creates a list of items from the database that's refreshed with each iteration. It then
         * makes an empty list to hold the names of items that have a quantity of zero and have
         * been notified about. Then the list of items is looped through and if it has a quantity of
         * zero and a notification hasn't been sent, an SMS is sent and the item's name is added to
         * the emptyItems list. The emptyItems list is then iterated through and any of the items
         * that are currently in there and have quantities higher than zero have their names removed
         * from the list. Then the whole process repeats itself until the app is closed.
         */
        List<Item> itemList;
        ArrayList<String> emptyItems = new ArrayList<>();
        while (true) {
            itemList = mInventory.itemDao().getItems();
            for (Item item:
                 itemList) {
                if (item.getQuantity() <= 0 && !emptyItems.contains(item.getName())) {
                    sendSmsNotif(item, number);
                    emptyItems.add(item.getName());
                }
                Iterator<String> itr = emptyItems.iterator();
                while (itr.hasNext()) {
                    String emptyItem = itr.next();
                    if (item.getName().equals(emptyItem) && item.getQuantity() > 0) {
                        itr.remove();
                    }
                }
            }
        }
    }

    public void sendSmsNotif(Item item, String number) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null,
                    getString(R.string.notifMessage, item.getName()),
                    null, null);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.notifSent, Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }
    }
}