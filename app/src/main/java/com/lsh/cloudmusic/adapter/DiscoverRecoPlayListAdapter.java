package com.lsh.cloudmusic.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lsh.cloudmusic.R;
import com.lsh.cloudmusic.network.baen.RecommendPlayList;


import org.jetbrains.annotations.NotNull;

public class DiscoverRecoPlayListAdapter extends BaseQuickAdapter<RecommendPlayList, BaseViewHolder> {

    public DiscoverRecoPlayListAdapter() {
        super(R.layout.item_discover_recommend_playlist);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, RecommendPlayList recommendPlayList) {
        ImageView imageView = baseViewHolder.getView(R.id.playListImg);
        Glide.with(imageView)
                .load(recommendPlayList.getPicUrl())
                .into(imageView);
        baseViewHolder.setText(R.id.playListName,recommendPlayList.getName());
    }
}
