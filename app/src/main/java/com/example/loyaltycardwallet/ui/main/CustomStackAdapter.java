package com.example.loyaltycardwallet.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

        CardProviderViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mImageView = view.findViewById(R.id.image_list_card_logo);

            if (colors == null) {
                colors = getContext().getResources().obtainTypedArray(R.array.card_color_list);
            }
        }

        @Override
        public void onItemExpand(boolean b) {
//                mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);

            CardProvider provider = (CardProvider) mImageView.getTag();

            if (provider != null) {
                if (b) {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(provider.barcode, BarcodeFormat.CODE_128, 140, 140);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                        mImageView.setImageBitmap(bitmap);

                        mContainerContent.setVisibility(View.VISIBLE);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (provider.getLogo() != null) {
                        mImageView.setImageBitmap(provider.getLogo());
                    }

                    mContainerContent.setVisibility(View.GONE);
                }
            }

        }

        void onBind(CardProvider provider) {
            mImageView.setImageBitmap(provider.getLogo());
            mImageView.setTag(provider);


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
