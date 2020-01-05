package com.example.loyaltycardwallet.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.Card.Card;
import com.example.loyaltycardwallet.data.Card.CardDataSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

public class CustomStackAdapter extends StackAdapter<Card> {

    CustomStackAdapter(Context context) {
        super(context);
    }


    @Override
    public void bindView(Card card, int position, CardStackView.ViewHolder holder) {
        CardViewHolder h = (CardViewHolder) holder;
        h.onBind(card);
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    static class CardViewHolder extends CardStackView.ViewHolder {
        static private TypedArray colors;
        static private int nextColorIndex = 0;

        View mLayout;
        View mContainerContent;
        ImageView mImageView;
        TextView mNameView;
        TextView mAddressView;
        TextView mIsOpenView;

        MenuItem add;
        MenuItem options;

        MainActivity activity;

        Card card;

        CardViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mImageView = view.findViewById(R.id.image_list_card_logo);

            mNameView = view.findViewById(R.id.card_textview_name);
            mAddressView = view.findViewById(R.id.card_textview_address);
            mIsOpenView = view.findViewById(R.id.card_textview_isOpen);

            activity = (MainActivity) view.getContext();

            add = activity.menu.findItem(R.id.action_add);
            options = activity.menu.findItem(R.id.card_edit);


            mContainerContent.setVisibility(View.GONE);

            if (colors == null) {
                colors = getContext().getResources().obtainTypedArray(R.array.card_color_list);
            }
        }

        @Override
        public void onItemExpand(boolean b) {
            if (card != null) {
                Menu menu = options.getSubMenu();


                menu.findItem(R.id.card_action_delete).setOnMenuItemClickListener(item -> {
                    new CardDataSource.delete(getContext(), card).execute();

                    // refresh list
                    activity.insertItemResponse(null);
                    return true;
                });


                if (b) {
                    if (card.barcodeBitmap == null) {
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(card.barcode, BarcodeFormat.CODE_128, 140, 140);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                            card.barcodeBitmap = bitmap;

                            mImageView.setImageBitmap(bitmap);

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    } else {
                        mImageView.setImageBitmap(card.barcodeBitmap);
                    }

                    mContainerContent.setVisibility(View.VISIBLE);

                    add.setVisible(false);
                    options.setVisible(true);

                } else {
                    if (card.logo != null) {
                        mImageView.setImageBitmap(card.logo);
                    }

                    mContainerContent.setVisibility(View.GONE);

                    add.setVisible(true);
                    options.setVisible(false);
                }
            }

        }

        void onBind(Card card) {
            this.card = card;

            mImageView.setImageBitmap(card.logo);

            Context context = getContext();

            mNameView.setText(context.getString(R.string.store_name, card.formated_name));
            mAddressView.setText(context.getString(R.string.store_address, card.address));
            mIsOpenView.setText(context.getString(
                    R.string.store_isOpen, card.isOpen ?
                            context.getString(R.string.isOpen) : context.getString(R.string.isClosed)
            ));


            if (card.colorIndex == -1) {
                if (!colors.hasValue(nextColorIndex)) {
                    nextColorIndex = 0;
                }

                card.colorIndex = nextColorIndex;
                nextColorIndex++;
            }

            mLayout.getBackground().setColorFilter(colors.getColor(card.colorIndex, 0), PorterDuff.Mode.SRC_IN);
        }

    }
}
