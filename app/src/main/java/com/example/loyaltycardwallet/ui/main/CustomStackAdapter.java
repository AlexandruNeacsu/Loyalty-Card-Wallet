package com.example.loyaltycardwallet.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loyaltycardwallet.R;
import com.example.loyaltycardwallet.data.CardProvider.CardProvider;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

public class CustomStackAdapter extends StackAdapter<CardProvider> {

    CustomStackAdapter(Context context) {
        super(context);
    }


    @Override
    public void bindView(CardProvider provider, int position, CardStackView.ViewHolder holder) {
        CardProviderViewHolder h = (CardProviderViewHolder) holder;
        h.onBind(provider);
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new CardProviderViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    static class CardProviderViewHolder extends CardStackView.ViewHolder {
        static private TypedArray colors;
        static private int nextColorIndex = 0;

        View mLayout;
        View mContainerContent;
        ImageView mImageView;
        TextView mNameView;
        TextView mAddressView;
        TextView mIsOpenView;

        CardProvider provider;

        CardProviderViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mImageView = view.findViewById(R.id.image_list_card_logo);

            mNameView = view.findViewById(R.id.card_textview_name);
            mAddressView = view.findViewById(R.id.card_textview_address);
            mIsOpenView = view.findViewById(R.id.card_textview_isOpen);

            mContainerContent.setVisibility(View.GONE);

            if (colors == null) {
                colors = getContext().getResources().obtainTypedArray(R.array.card_color_list);
            }
        }

        @Override
        public void onItemExpand(boolean b) {
//                mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);

            if (provider != null) {
                if (b) {
                    if (provider.barcodeBitmap == null) {
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(provider.barcode, BarcodeFormat.CODE_128, 140, 140);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                            provider.barcodeBitmap = bitmap;

                            mImageView.setImageBitmap(bitmap);

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    } else {
                        mImageView.setImageBitmap(provider.barcodeBitmap);
                    }

                    mContainerContent.setVisibility(View.VISIBLE);

                } else {
                    if (provider.getLogo() != null) {
                        mImageView.setImageBitmap(provider.getLogo());
                    }

                    mContainerContent.setVisibility(View.GONE);
                }
            }

        }

        void onBind(CardProvider provider) {
            this.provider = provider;

            mImageView.setImageBitmap(provider.getLogo());

            Context context = getContext();

            mNameView.setText(context.getString(R.string.store_name, provider.getFormated_name()));
            mAddressView.setText(context.getString(R.string.store_address, provider.getAddress()));
            mIsOpenView.setText(context.getString(
                    R.string.store_isOpen, provider.getOpen() ?
                            context.getString(R.string.isOpen) : context.getString(R.string.isClosed)
            ));


            if (provider.colorIndex == -1) {
                if (!colors.hasValue(nextColorIndex)) {
                    nextColorIndex = 0;
                }

                provider.colorIndex = nextColorIndex;
                nextColorIndex++;
            }

            mLayout.getBackground().setColorFilter(colors.getColor(provider.colorIndex, 0), PorterDuff.Mode.SRC_IN);
        }

    }
}
