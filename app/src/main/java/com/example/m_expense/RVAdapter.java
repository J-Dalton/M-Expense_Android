package com.example.m_expense;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private final RVInterface recyclerviewInterface;
    private final LayoutInflater rowInflater;
    private static Cursor cursor;
    private final String viewIdentifier;


    RVAdapter(Context context, RVInterface recyclerviewInterface, Cursor cursor, String viewIdentifier) {
        this.rowInflater = LayoutInflater.from(context);
        this.recyclerviewInterface = recyclerviewInterface;
        RVAdapter.cursor = cursor;
        this.viewIdentifier = viewIdentifier;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        if (viewIdentifier.equals("trip")) {
            view = rowInflater.inflate(R.layout.trip_row_layout, parent, false);

        }
        if (viewIdentifier.equals("expense")) {
            view = rowInflater.inflate(R.layout.expense_row_layout, parent, false);

        }
        if (viewIdentifier.equals("search")) {
            view = rowInflater.inflate(R.layout.search_row_layout, parent, false);

        }

        return new ViewHolder(view, recyclerviewInterface);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }


        if (viewIdentifier.equals("trip") || viewIdentifier.equals("search")) {
            @SuppressLint("Range") String tripName = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_NAME_COLUMN));
            @SuppressLint("Range") String tripDate = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_DATE_COLUMN));
            @SuppressLint("Range") String tripDestination = cursor.getString(cursor.getColumnIndex(DBHelper.TRIP_DESTINATION_COLUMN));
            @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(DBHelper.TRIP_ID_COLUMN));
            @SuppressLint("Range") long flagId = cursor.getLong(cursor.getColumnIndex(DBHelper.TRIP_FLAG_COLUMN));




            if (viewIdentifier.equals("search")) {
                if (flagId == 1) {
                    holder.flagIconSearch.setImageResource(R.drawable.unitedkingdom);
                }
                if (flagId == 2) {
                    holder.flagIconSearch.setImageResource(R.drawable.france);
                }
                if (flagId == 3) {
                    holder.flagIconSearch.setImageResource(R.drawable.germany);
                }
                if (flagId == 4) {
                    holder.flagIconSearch.setImageResource(R.drawable.spain);
                }

                holder.txtTripNameSearch.setText(tripName);
                holder.txtTripDateSearch.setText(tripDate);
                holder.txtTripDestinationSearch.setText(tripDestination);
                holder.itemView.setTag(id);
            }

            if (viewIdentifier.equals("trip")) {
                if (flagId == 1) {
                    holder.flagIcon.setImageResource(R.drawable.unitedkingdom);
                }
                if (flagId == 2) {
                    holder.flagIcon.setImageResource(R.drawable.france);
                }
                if (flagId == 3) {
                    holder.flagIcon.setImageResource(R.drawable.germany);
                }
                if (flagId == 4) {
                    holder.flagIcon.setImageResource(R.drawable.spain);
                }
                holder.txtTripName.setText(tripName);
                holder.txtTripDate.setText(tripDate);
                holder.txtTripDestination.setText(tripDestination);
                holder.itemView.setTag(id);
            }


        }
        if (viewIdentifier.equals("expense")) {
            @SuppressLint("Range") String exAmount = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_AMOUNT_COLUMN));
            @SuppressLint("Range") String exTime = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_TIME_COLUMN));
            @SuppressLint("Range") String exComments = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_COMMENTS_COLUMN));
            @SuppressLint("Range") String exType = cursor.getString(cursor.getColumnIndex(DBHelper.EXPENSE_TYPE_COLUMN));
            @SuppressLint("Range") int exIcon = cursor.getInt(cursor.getColumnIndex(DBHelper.EXPENSE_ICON_COLUMN));
            @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(DBHelper.EXPENSE_ID_COLUMN));

            holder.txtExComments.setText(exComments);
            holder.txtExTime.setText(exTime);
            holder.txtExAmount.setText("£"+exAmount);
            holder.txtExType.setText(exType);
            holder.itemView.setTag(id);

            if (exIcon == 1) {
                holder.exIcon.setImageResource(R.drawable.hotel_icon);
            }
            if (exIcon == 2) {
                holder.exIcon.setImageResource(R.drawable.transit_icon);
            }
            if (exIcon == 3) {
                holder.exIcon.setImageResource(R.drawable.food_icon);
            }


        }


    }


    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void resetCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtExType;
        TextView txtExAmount;
        TextView txtExTime;
        TextView txtExComments;

        TextView txtTripName;
        TextView txtTripDate;
        TextView txtTripDestination;

        TextView txtTripNameSearch;
        TextView txtTripDateSearch;
        TextView txtTripDestinationSearch;

        ImageView exIcon;
        ImageView flagIcon;
        ImageView flagIconSearch;

        CardView cardview;

        public ViewHolder(View itemView, RVInterface recyclerviewInterface) {
            super(itemView);

            txtExType = itemView.findViewById(R.id.tvExType);
            txtExAmount = itemView.findViewById(R.id.tvExAmount);
            txtExTime = itemView.findViewById(R.id.tvExTime);
            txtExComments = itemView.findViewById(R.id.tvExComments);
            exIcon = itemView.findViewById(R.id.imgExpenseIcon);

            txtTripName = itemView.findViewById(R.id.tvTripName);
            txtTripDate = itemView.findViewById(R.id.tvTripDate);
            txtTripDestination = itemView.findViewById(R.id.tvTripDestination);
            flagIcon = itemView.findViewById(R.id.imgTripFlag);
            flagIconSearch = itemView.findViewById(R.id.imgTripFlagSearch);

            txtTripNameSearch = itemView.findViewById(R.id.tvTripNamesearch);
            txtTripDateSearch = itemView.findViewById(R.id.tvTripDatesearch);
            txtTripDestinationSearch = itemView.findViewById(R.id.tvTripDestinationsearch);

            cardview = itemView.findViewById(R.id.cardTrip);

            itemView.setOnLongClickListener(view -> {
                if (recyclerviewInterface != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerviewInterface.onItemLongClick(((long) itemView.getTag()));

                    }
                }
                return true;
            });
        }
    }
}
