package com.fotile.c2i.activity.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fotile.c2i.activity.music.R;
import com.fotile.c2i.activity.music.MusicAlbumViewHolder;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * 文件名称：MusicTrackRecyclerAdapter
 * 创建时间：17-9-12 下午4:48
 * 文件作者：zhangqiang
 * 功能描述：搜索界面的adapter
 */
public class MusicSearchRecyclerAdapter extends RecyclerView.Adapter<MusicAlbumViewHolder> {

    private Context context;
    private SearchTrackList searchTrackList = new SearchTrackList();

    public MusicSearchRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MusicAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_search_album, parent, false);
        MusicAlbumViewHolder holder = new MusicAlbumViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MusicAlbumViewHolder holder, int position) {
        holder.musicName.setText(searchTrackList.getTracks().get(position).getTrackTitle());
        holder.musicAuthor.setText(searchTrackList.getTracks().get(position).getAnnouncer().getNickname());
        Glide.with(context).load(searchTrackList.getTracks().get(position).getCoverUrlLarge())
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 0,
                        RoundedCornersTransformation.CornerType.ALL)).into(holder.musicCover);
    }

    @Override
    public int getItemCount() {
        if (searchTrackList.getTracks() == null) {
            return 0;
        }
        return searchTrackList.getTracks().size();
    }

    public void setSearchTrackList(SearchTrackList searchTrackList) {
        this.searchTrackList = searchTrackList;

    }

}
