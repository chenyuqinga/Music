package com.fotile.c2i.activity.music;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 文件名称：MusicAlbumViewHolder
 * 创建时间：17-9-12 下午2:00
 * 文件作者：zhangqiang
 * 功能描述：
 */
public class MusicAlbumViewHolder extends RecyclerView.ViewHolder {


    public ImageView musicCover;
    public TextView musicName;
    public TextView musicAuthor;

    public MusicAlbumViewHolder(View itemView) {
        super(itemView);
        musicCover = (ImageView) itemView.findViewById(R.id.img_music);
        musicName = (TextView) itemView.findViewById(R.id.tv_music_name);
        musicAuthor = (TextView) itemView.findViewById(R.id.tv_music_author);

    }
}
