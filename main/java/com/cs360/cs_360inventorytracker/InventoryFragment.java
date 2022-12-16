package com.cs360.cs_360inventorytracker;

/*
 * This fragment shows the inventory and keeps the inventory accurate and updated. The various
 * interactive elements (such as increment, decrement, and delete) in each item listing are handled
 * and listened to here.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class InventoryFragment extends Fragment {

    public interface OnItemSelectedListener {
        void onItemSelected(String itemName);
    }

    private OnItemSelectedListener mListener;

    protected RecyclerView recyclerView;

    private InventoryDatabase mInventoryDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        mInventoryDb = InventoryDatabase.getInstance(getContext());

        recyclerView = view.findViewById(R.id.invenRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemAdapter adapter = new ItemAdapter(mInventoryDb.itemDao().getItems());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ItemAdapter adapter = new ItemAdapter(mInventoryDb.itemDao().getItems());
        recyclerView.setAdapter(adapter);
    }

    private class ItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Item mItem;
        private ImageButton mDelItemButton;
        private TextView mItemName;
        private TextView mItemQuantity;
        private ImageButton mIncrementButton;
        private ImageButton mDecrementButton;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.inven_item, parent, false));

            mDelItemButton = (ImageButton) itemView.findViewById(R.id.delItem);
            mItemName = (TextView) itemView.findViewById(R.id.itemName);
            mItemQuantity = (TextView) itemView.findViewById(R.id.itemQuantity);
            mIncrementButton = (ImageButton) itemView.findViewById(R.id.incrementButton);
            mDecrementButton = (ImageButton) itemView.findViewById(R.id.decrementButton);

            itemView.setOnClickListener(this);
            mDelItemButton.setOnClickListener(this);
            mIncrementButton.setOnClickListener(this);
            mDecrementButton.setOnClickListener(this);
        }

        public void bind(Item item) {
            mItem = item;
            mItemName.setText(mItem.getName());
            mItemQuantity.setText(String.valueOf(mItem.getQuantity()));
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == mDelItemButton.getId()) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.itemDelete)
                        .setMessage(getString(R.string.itemDeleteMessage, mItem.getName()))
                        .setPositiveButton(R.string.itemDeleteAllow, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mInventoryDb.itemDao().deleteItem(mItem);

                                ItemAdapter adapter = new ItemAdapter(mInventoryDb.itemDao().getItems());
                                recyclerView.setAdapter(adapter);
                            }
                        }).setNegativeButton(R.string.itemDeleteCancel, null)
                        .create()
                        .show();
            }
            else if (view.getId() == mIncrementButton.getId()) {
                mItem.setQuantity(mItem.getQuantity() + 1);
                mInventoryDb.itemDao().updateItem(mItem);
                recyclerView.getAdapter().notifyItemChanged(this.getAdapterPosition());
            }
            else if (view.getId() == mDecrementButton.getId()) {
                mItem.setQuantity(mItem.getQuantity() - 1);
                mInventoryDb.itemDao().updateItem(mItem);
                recyclerView.getAdapter().notifyItemChanged(this.getAdapterPosition());
            }
            else {
                mListener.onItemSelected(mItem.getName());
            }
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<Item> mItems;

        public ItemAdapter(List<Item> items) {
            mItems = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Item item = mItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            mListener = (OnItemSelectedListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}