package com.example.loyaltycardwallet.ui.CardProviderList;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.example.loyaltycardwallet.data.CardProvider.CardProviderDataSource;
import com.example.loyaltycardwallet.ui.CardProviderList.CardProviderFragment.OnListFragmentInteractionListener;

import java.util.List;

public class CardProviderAdapter extends RecyclerView.Adapter<CardProviderAdapter.ViewHolder> {

    private final List<CardProvider> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final CardProviderAdapter self = this;

    public CardProviderAdapter(List<CardProvider> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cardprovider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mProviderLogoView.setImageBitmap(mValues.get(position).logo);
        holder.mProviderNameView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mProviderLogoView;
        final TextView mProviderNameView;
        CardProvider mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mProviderLogoView = view.findViewById(R.id.providerLogo);
            mProviderNameView = view.findViewById(R.id.providerName);

            Button button = view.findViewById(R.id.delete_provider_button);
            button.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());

                builder.setMessage(R.string.delete_provider);

                builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                    new CardProviderDataSource.delete(view.getContext(), mItem).execute();

                    mValues.remove(mItem);

                    self.notifyDataSetChanged();
                });
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });


                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mProviderNameView.getText() + "'";
        }
    }
}
