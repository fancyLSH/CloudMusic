package com.lsh.cloudmusic.custom.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lsh.cloudmusic.R;
import com.lsh.cloudmusic.adapter.PlayListDialogAdapter;
import com.lsh.cloudmusic.entity.PlayerSong;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import fun.inaction.dialog.ViewAdapter;

public class PlayListDialogContent implements ViewAdapter {

    private ViewGroup parent;
    private View view;
    private RecyclerView recyclerView;
    private TextView sizeTextView;
    private PlayListDialogAdapter adapter = new PlayListDialogAdapter();
    private OnClickListener onClickListener;

    public PlayListDialogContent(ViewGroup parent) {
        this.parent = parent;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_content,parent,false);
        sizeTextView = view.findViewById(R.id.playListSizeText);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        adapter.setOnItemClickListener((adapter, view, position) -> {
            ((PlayListDialogAdapter)adapter).setSelectedItem(position);
            if(onClickListener != null){
                onClickListener.onClick(position);
            }
        });
    }

    @NotNull
    @Override
    public View getView() {
        return view;
    }

    public void setSize(int size){
        sizeTextView.setText("("+size+")");
    }

    public void setListData(List<PlayerSong> songs){
        adapter.setNewInstance(songs);
    }

    public void setSelectedItem(int index){
        adapter.setSelectedItem(index);
        recyclerView.scrollToPosition(index);
    }

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public static interface OnClickListener{
        void onClick(int position);
    }

}
