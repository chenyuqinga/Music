package com.fotile.c2i.activity.music.message;

/**
 * 文件名称：FinishActivityMessage
 * 创建时间：2018/2/28 16:23
 * 文件作者：yaohx
 * 功能描述：关闭某一个activity
 */
public class FinishActivityMessage {
    public Class _class;

    public FinishActivityMessage(Class _class) {
        this._class = _class;
    }

}
