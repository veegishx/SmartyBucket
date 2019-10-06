package com.saphyrelabs.smartybucket.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.saphyrelabs.smartybucket.R;
import com.saphyrelabs.smartybucket.RegisterItems;
import com.saphyrelabs.smartybucket.model.Item;

public class ItemAdapter extends FirestoreAdapter<ItemAdapter.ViewHolder> {

    public interface onItemSelectedListener {

        void onItemSelected(DocumentSnapshot item);

    }

    private onItemSelectedListener mListener;

    public ItemAdapter(Query query, onItemSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.activity_register_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView categoryView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            nameView = itemView.findViewById(R.id.item_name);
            priceView = itemView.findViewById(R.id.item_price);
            categoryView = itemView.findViewById(R.id.item_category);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final onItemSelectedListener listener) {

            Item item = snapshot.toObject(Item.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext())
                    .load(item.getItemImageUrl())
                    .into(imageView);

            nameView.setText(item.getItemName());
            categoryView.setText(item.getItemCategory());
            priceView.setText("Rs " + item.getItemPrice());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemSelected(snapshot);
                    }
                }
            });
        }
    }
}
