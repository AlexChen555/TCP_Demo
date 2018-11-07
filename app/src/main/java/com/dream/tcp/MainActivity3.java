package com.dream.tcp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.czh.adapter.DataBeanAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity3 extends AppCompatActivity {

    private static final String TAG = "TcpDemo";

    boolean isStart = true;

    @Bind(R.id.btn_start_client)       Button               mBtn_start;
    @Bind(R.id.btn_request)            Button               mBtn_request;
    @Bind(R.id.btn_stop)               Button               mBtn_stop;
    @Bind(R.id.btn_03)                 Button               mBtn_03;
    @Bind(R.id.ed_client_ip)           EditText             mEdClientIp;
    @Bind(R.id.ed_client_port)         EditText             mEdClientPort;
    @Bind(R.id.ed_client_send_message) EditText             mEdClientSendMessage;
    @Bind(R.id.lv_client_msg)          ListView             mLvClientMsg;
    private                            List<String>         mClientMessageList;
    private                            ArrayAdapter<String> mClientMessageAdapter;
    private                            List<DataBean>         mDataBeanList;
    private                            DataBeanAdapter        mDataBeanAdapter;
    private                            String               mClientIp;
    private                            int                  mClientPort;
    private                            String               mClientSendMessage;
    private                            String               mClientInetAddress;//服务端地址
    private                            String               mClientLocalAddress;//客户端地址
    private                            TcpClient            mTcpClient;

    private boolean isConected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mClientMessageList = new ArrayList<>();
        mClientMessageAdapter = new ArrayAdapter<>(this, R.layout.item_log, R.id.item_log, mClientMessageList);
        mLvClientMsg.setAdapter(mClientMessageAdapter);

        LogWawa.i("isWifi=========>>" + isWifi(this));

//        mDataBeanList = new ArrayList<>();//测试数据
//        for (int i = 1; i < 15; i++) {
//            DataBean dataBean = new DataBean(i+"","超声波"+i,"port"+i,(i + .02)+"");
//            mDataBeanList.add(dataBean);
//        }
//        mDataBeanAdapter = new DataBeanAdapter(this, mDataBeanList);
//        mLvClientMsg.setAdapter(mDataBeanAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private void getClientPort() {
        mClientPort = Integer.parseInt(mEdClientPort.getText().toString().trim());
    }

    private void getClientIp() {
        mClientIp = mEdClientIp.getText().toString().trim();
    }

    private void getClientSendMessage() {
        mClientSendMessage = mEdClientSendMessage.getText().toString().trim();
    }

    @OnClick({
                     R.id.btn_start_client,//连接服务
                     R.id.btn_client_send,//发送消息
                     R.id.btn_request,//请求
                     R.id.btn_stop,//Stop
                     R.id.btn_03,//
                     R.id.btn_client_clear_message//清除日志
             })
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_start_client://连接服务

                getClientIp();
                getClientPort();

                mTcpClient = TcpClient.getInstance();
                mTcpClient.setIp(mClientIp);
                mTcpClient.setPort(mClientPort);

                mTcpClient.setOnConnectListener(new TcpClient.OnConnectListener() {
                    @Override
                    public void onConnectSuccess(String inetAddress, String localAddress) {
                        mClientInetAddress = inetAddress;
                        mClientLocalAddress = localAddress;
                        String log = getTime()
                                + " 服务端地址：" + mClientInetAddress
                                + " 客户端地址：" + mClientLocalAddress + "\n"
                                + " 连接成功";
                        isConected = true;
                        logClient(log);
                        LogWawa.i(log);
                    }

                    @Override
                    public void onConnectFail(Throwable e, String message) {
                        String log = getTime()
                                + " 服务端地址：" + mClientInetAddress
                                + " 客户端地址：" + mClientLocalAddress + "\n"
                                + message;
                        logClient(log);
                        LogWawa.i(log);
                    }

                    @Override
                    public void onConnectError(Throwable e, String message) {
                        String log = getTime()
                                + " 服务端地址：" + mClientInetAddress
                                + " 客户端地址：" + mClientLocalAddress + "\n"
                                + message;
                        logClient(log);
                        LogWawa.i(log);

                    }

                    @Override
                    public void onMessage(String message) {
                        String log = getTime()
                                + " 服务端地址：" + mClientInetAddress
                                + " 客户端地址：" + mClientLocalAddress + "\n"
                                + message;
                        logClient(log);
                        LogWawa.i(log);
                    }
                });

                mTcpClient.connect();



                break;
            case R.id.btn_client_send://发送消息
//                getClientSendMessage();
//                if (mTcpClient != null) {
//                    mTcpClient.sendMessage(mClientSendMessage);
//                }
                if (mReThread != null && mReThread.isAlive()){
                    LogWawa.i("=========>>mThread != null && mThread.isAlive()");
                }else {//复位，并防止重复开启新线程
                    LogWawa.e("=========>>mThread  null or mThread not Alive()");
                    reFlag = false;reRequestData();
                }
                break;
            case R.id.btn_client_clear_message://清除日志
                mClientMessageList.clear();
                mClientMessageAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_request: {//
                if (isWifi(this)){//已经连接上wifi
                    if (isConected){//已经连接上服务端
                        String value = mEdClientSendMessage.getText().toString().trim();
                        if (null == value || value.length() == 0) {
                            Toast.makeText(this,"请输入内容！！",3).show();return;
                        }
                        if (mReThread != null && mReThread.isAlive()){
                            LogWawa.i("=========>>mReThread != null && mReThread.isAlive()");
                        }else {//复位，并防止重复开启新线程
                            LogWawa.e("=========>>mReThread  null or mReThread not Alive()");
                            reFlag = false;reRequestData();
                        }
//                requestData();
//                if (mThread != null && mThread.isAlive()){
//                    LogWawa.i("=========>>mThread != null && mThread.isAlive()");
//                }else {//复位，并防止重复开启新线程
//                    LogWawa.e("=========>>mThread  null or mThread not Alive()");
//                    flag = false;requestData();
//                }
                    }else {
                        Toast.makeText(this,"请先连接服务端！",3).show();
                    }

                }else {
                    Toast.makeText(this,"请先连接wifi！！",3).show();
                }

                break;
            }
            case R.id.btn_stop://
//                mDataBeanList.remove(1);
//                mDataBeanAdapter.notifyDataSetChanged();
                reFlag = true;Toast.makeText(this,"已停止请求",2).show();
                break;
            case R.id.btn_03://
//                mDataBeanList.clear();
//                mDataBeanAdapter.notifyDataSetChanged();
                int aa = 1;
                if (index == 3){
                    index = 0;
                }
                aa = ms[index];
                time = aa;
                index++;
                String text_str = aa + "ms";
                mBtn_03.setText(text_str);

                break;
        }
    }

    int[]  ms = {200, 500, 1000};
    int index = 0;

    int time = 1000;
    int count = 1;
    boolean flag = false;
    Thread mThread = null;
    private void requestData(){
        mThread = new Thread(new Runnable() {
            public void run() {
                while (count <= 20 && !flag) {
                    try {
                        System.out.println("value------>>" + count);

                        final DataBean dataBean = new DataBean(count+"","红外反光"+count,"port"+count,(count + .32)+"");
                        runOnUiThread(new Runnable() {//更新UI需要在UI主线程上，否则会报错
                            @Override
                            public void run() {
                                mDataBeanList.add(0,dataBean);
                                mDataBeanAdapter.notifyDataSetChanged();
                            }
                        });
                        count++;
                        if (count >= 20){ flag = true;count = 1;}
//                        Thread.sleep(2000);
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }); mThread.start();
    }


    boolean reFlag = false;
    Thread mReThread = null;
    private void reRequestData(){//模拟重复请求消息
        mReThread = new Thread(new Runnable() {
            public void run() {
                while (!reFlag) {
                    try {
                        getClientSendMessage();
                        if (mTcpClient != null) {
                            mTcpClient.sendMessage(mClientSendMessage);
                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {//模拟重复请求消息
//
//                                String aa = "发送消息：" + mClientSendMessage;
//                                logClientReal(aa);
//                            }
//                        });
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }); mReThread.start();
    }

    private void logClient(final String log) {
        if (!isCurrentlyOnMainThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logClientReal(log);
                }
            });
        } else {
            logClientReal(log);
        }
    }

    private void logClientReal(final String log) {
//        Log.d(TAG, log);
        mClientMessageList.add(0, log);
        mClientMessageAdapter.notifyDataSetChanged();
    }

    /**
     * 判断当前线程是否在主线程
     */
    private boolean isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取当前时间
     */
    private String getTime() {
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        return format.format(new Date());
    }

    /**
     * 获取WIFI下ip地址
     */
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo    wifiInfo    = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                             (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                             (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    /**
     * make true current connect is wifi
     * 当前网络连接是否为wifi  true为是
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean isOpenWifi(Context mContext) {

        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = manager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
