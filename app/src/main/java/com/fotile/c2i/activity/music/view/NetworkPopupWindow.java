package com.fotile.c2i.activity.music.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fotile.c2i.activity.music.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文件名称：NetworkPopupWindow
 * 创建时间：2018-01-31 10:51
 * 文件作者：shihuijuan
 * 功能描述：网络异常提示
 */

public class NetworkPopupWindow extends PopupWindow implements View.OnClickListener {

    /**
     * 去连接网络按钮
     */
    @BindView(R.id.tv_window_connect_network)
    TextView tvConnectNetwork;

    private View view;
    private Context context;

    public NetworkPopupWindow(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_network_popup_window,null);
        setContentView(view);
        ButterKnife.bind(this, view);
        initView();

    }

    private void initView(){
        this.setFocusable(true);
        tvConnectNetwork.setOnClickListener(this);
    }

    /**
     * 设置弹框大小
     * @param width
     * @param height
     */
    public void setWindowSize(int width, int height){
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(context, SettingActivity.class);
//        context.startActivity(intent);
    }

    /**
     * 注册监听网络状态变化
     */
    private void registerNetworkReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)){
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();
                if (state == NetworkInfo.State.CONNECTED){
                    dismiss();
                }
            }
        }
    };

    @Override
    public void dismiss() {
        context.unregisterReceiver(networkReceiver);
        super.dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        registerNetworkReceiver();
    }

}
