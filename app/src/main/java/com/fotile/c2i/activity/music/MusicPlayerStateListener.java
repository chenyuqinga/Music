package com.fotile.c2i.activity.music;

import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

/**
 * Created by yaohx on 2017/11/17.
 */

public abstract class MusicPlayerStateListener implements IXmPlayerStatusListener {
    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {

    }


    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int i) {

    }


    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }


}
