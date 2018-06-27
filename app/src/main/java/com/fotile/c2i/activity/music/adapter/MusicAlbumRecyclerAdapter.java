package com.fotile.c2i.activity.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.MusicAlbumViewHolder;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 文件名称：MusicAlbumRecyclerAdapter
 * 创建时间：17-9-11 上午9:33
 * 文件作者：zhangqiang
 * 功能描述：专辑的adapter
 */
public class MusicAlbumRecyclerAdapter extends RecyclerView.Adapter<MusicAlbumViewHolder> {

    private Context context;

    private AlbumList albumList = new AlbumList();


    public MusicAlbumRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MusicAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_music_album, parent, false);
        MusicAlbumViewHolder holder = new MusicAlbumViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(MusicAlbumViewHolder holder, int position) {

        holder.musicName.setText(albumList.getAlbums().get(position).getAlbumTitle());
        Glide.with(context).load(albumList.getAlbums().get(position).getCoverUrlLarge())
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 0,
                        RoundedCornersTransformation.CornerType.ALL)).into(holder.musicCover);
    }


    @Override
    public int getItemCount() {
        if (albumList == null || albumList.getAlbums() == null) {
            return 0;
        }
        return albumList.getAlbums().size();
    }

    public void setAlbumTrackList(AlbumList albumList) {
        this.albumList = albumList;

    }

}
