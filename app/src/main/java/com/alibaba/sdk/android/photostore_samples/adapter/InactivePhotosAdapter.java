/**
 * Copyright (C) 2017 Alibaba Group Holding Limited
 */
package com.alibaba.sdk.android.photostore_samples.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.photostore_samples.MyApplication;
import com.alibaba.sdk.android.photostore_samples.R;
import com.alibaba.sdk.android.photostore_samples.model.MyPhoto;
import com.alibaba.sdk.android.photostore_samples.util.ThumbnailLoader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class InactivePhotosAdapter extends HeaderFooterAdapter {

    private enum TYPE {
        HEADER,
        FOOTER,
        CONTENT
    }

    private List<MyPhoto> datas = new ArrayList<>();

    String TAG = InactivePhotosAdapter.class.getSimpleName();

    public InactivePhotosAdapter(Context context, int gridCount) {
        super(context, gridCount);
        isActionMode = true;
    }

    public void setData(List<MyPhoto> photos) {
        datas.clear();
        datas.addAll(photos);
        selects.clear();
        for (MyPhoto p : photos) {
            selects.add(false);
        }
    }

    @Override
    public void destroy() {
        datas.clear();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE.CONTENT.ordinal()) {
            return new PhotoViewHolder((ViewGroup) mLayoutInflater.inflate(R.layout.grid_face, parent, false));
        }
        return null;
    }

    @Override
    protected void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhotoViewHolder) {
            final MyPhoto p = datas.get(position);
            final PhotoViewHolder h = (PhotoViewHolder) holder;
            h.position = position;

            Log.d(TAG, "autoCleanEnabled " + MyApplication.autoCleanEnabled);
            Log.d(TAG, "p.inactiveTime " + p.inactiveTime);
            if (MyApplication.autoCleanEnabled && p.inactiveTime > 0) {
                Long dayMillis = 24*60*60*1000L;
                Long expireTime = (System.currentTimeMillis() - p.inactiveTime);
                Long leftDays = MyApplication.autoCleanDays - expireTime/dayMillis;
                Log.d(TAG, "expireTime " + expireTime);
                Log.d(TAG, "leftDays " + leftDays);

                h.ivName.setTextColor(Color.WHITE);
                if (leftDays == 1) {
                    Long millis = expireTime%dayMillis;
                    if (millis > 0) {
                        h.ivName.setText("<" + String.valueOf(leftDays) + "天");
                    }
                }
                else {
                    h.ivName.setText(String.valueOf(leftDays) + "天");
                }
            } else {
                h.ivName.setText("");
            }

            ThumbnailLoader.getInstance().loadByPhotoId(h.ivPhoto, p.id, 512, 512, p.isVideo);

            if (isActionMode) {
                h.checkBox.setVisibility(View.VISIBLE);
                if (selects.get(position)) {
                    h.checkBox.setButtonDrawable(R.drawable.btn_check_on);
                    h.ivPhoto.setScaleX(0.8f);
                    h.ivPhoto.setScaleY(0.8f);
                    h.ivMask.setScaleX(0.8f);
                    h.ivMask.setScaleY(0.8f);
                } else {
                    h.checkBox.setButtonDrawable(R.drawable.btn_check_off);
                    h.ivPhoto.setScaleX(1f);
                    h.ivPhoto.setScaleY(1f);
                    h.ivMask.setScaleX(1f);
                    h.ivMask.setScaleY(1f);
                }
            } else {
                h.checkBox.setVisibility(View.INVISIBLE);
                h.ivPhoto.setScaleX(1f);
                h.ivPhoto.setScaleY(1f);
                h.ivMask.setScaleX(1f);
                h.ivMask.setScaleY(1f);
            }
        }
    }

    @Override
    protected int getContentItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    protected int getContentItemViewType(int position) {
        return TYPE.CONTENT.ordinal();
    }

    @Override
    protected int getContentSpanSize(int position) {
        return 1;
    }

    @Override
    protected void startActionMode() {

    }

    @Override
    protected void finishActionMode() {

    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        @BindView(R.id.iv_mask)
        ImageView ivMask;

        @BindView(R.id.checkBox)
        CheckBox checkBox;

        @BindView(R.id.iv_name)
        TextView ivName;

        int position = 0;

        public PhotoViewHolder(ViewGroup viewGroup) {
            super(viewGroup);
            ButterKnife.bind(this, viewGroup);
        }

        void toggle() {
            if (selects.get(position)) {
                checkBox.setButtonDrawable(R.drawable.btn_check_off);
                ivPhoto.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                ivMask.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            } else {
                checkBox.setButtonDrawable(R.drawable.btn_check_on);
                ivPhoto.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start();
                ivMask.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start();
            }
            selects.set(position, !selects.get(position));
        }

        @OnClick(R.id.iv_mask)
        void onItemClick() {
            if (isActionMode) {
                toggle();
            }
        }

        @OnClick(R.id.checkBox)
        void onCheckClick() {
            toggle();
        }

        @OnLongClick(R.id.iv_mask)
        boolean onItemLongClick() {
            if (!isActionMode) {
                startActionMode();
                toggle();
                return true;
            }
            return false;
        }
    }

    public void selectAll(boolean all) {
        for (int i = 0; i < selects.size(); i++) {
            selects.set(i, all);
        }
        notifyDataSetChanged();
    }

    public List<MyPhoto> getSelected() {
        List<MyPhoto> list = new ArrayList<>();
        for (int i = 0; i < selects.size(); i++) {
            if (selects.get(i)) {
                list.add(datas.get(i));
            }
        }
        return list;
    }

}
