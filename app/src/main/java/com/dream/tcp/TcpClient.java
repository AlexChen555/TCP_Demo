package com.dream.tcp;

import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import java.net.SocketTimeoutException;

public class TcpClient {
    private static final String    TAG        = "TcpClient";
    private static       TcpClient mTcpClient = new TcpClient();
    private ClientSocketThread mClientSocketThread;
    private OnConnectListener  mOnConnectListener;

    public static TcpClient getInstance() {
        return mTcpClient;
    }


    public void setOnConnectListener(OnConnectListener onConnectListener) {
        mOnConnectListener = onConnectListener;
    }

    public interface OnConnectListener {
        /**
         * @param inetAddress  服务端地址
         * @param localAddress 客户端地址
         */
        void onConnectSuccess(String inetAddress, String localAddress);

        void onConnectFail(Throwable e, String message);

        void onConnectError(Throwable e, String message);

        void onMessage(String message);
    }

    public void setIp(String ip) {
        mClientIp = ip;
    }

    public void setPort(int port) {
        mClientPort = port;
    }

    public void connect() {
        if (mOnConnectListener == null) {
            throw new RuntimeException("请设置OnConnectListener");
        }
        if (mClientIp == null || mClientIp.length() == 0 || mClientPort == 0) {
            mOnConnectListener.onConnectFail(new RuntimeException("请设置ip与port"), "请设置ip与port");
            return;
        }
        if (mClientSocketThread == null) {
            mClientSocketThread = new ClientSocketThread();
            mClientSocketThread.start();
        } else {
            mOnConnectListener.onConnectError(new RuntimeException("已经建立连接"), "已经建立连接");
        }
    }

    public void disconnect() {
        if (mOnConnectListener == null) {
            throw new RuntimeException("请设置OnConnectListener");
        }
        if (mClientSocket != null) {
            try {
                mClientSocket.close();
                mClientIn.close();
                mClientOut.close();
                mClientOut = null;
                mClientIn = null;
                mClientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mOnConnectListener.onConnectError(new RuntimeException("已经断开连接"), "已经断开连接");
        }
        if (mClientSocketThread != null) {
            mClientSocketThread = null;
        }
    }

    public void sendMessage(String message) {
        if (mOnConnectListener == null) {
            throw new RuntimeException("请设置OnConnectListener");
        }
        mClientSendMessage = message;
        clientSendMessage();
    }

    private void insideDisconnect() {
        LogWawa.i("insideDisconnect!!!");
        if (mClientSocket != null) {
            try {
                mClientSocket.close();
                mClientIn.close();
                mClientOut.close();
                mClientOut = null;
                mClientIn = null;
                mClientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mClientSocketThread != null) {
            mClientSocketThread = null;
        }
    }
    //=================================================================================
    // Client
    //=================================================================================

    private String         mClientIp;
    private int            mClientPort;
    private Socket         mClientSocket;
    private BufferedReader mClientIn;
    private PrintWriter    mClientOut;
    private String         mClientSendMessage;

    class ClientSocketThread extends Thread {

        @Override
        public void run() {
            try {
                mClientSocket = new Socket(mClientIp, mClientPort);
                mClientSocket.setKeepAlive(true);//+
                mClientIn = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                mClientOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream())), true);
                ClientReceiverRunnable runnable = new ClientReceiverRunnable(mClientSocket);
                new Thread(runnable).start();//开启实时监听接收消息的线程
                // (有个奇怪的问题，服务端发送消息过来时，需要数据末加\n \r等结尾才能识别并接收到，待后续优化）
                if (mOnConnectListener != null) {
                    mOnConnectListener.onConnectSuccess(mClientSocket.getInetAddress().getHostAddress(), mClientSocket.getLocalAddress().getHostAddress());

//                    mClientOut.println("11");//test测试发送消息
                }

            } catch (Exception e){
                if (e instanceof SocketTimeoutException) {
                    mOnConnectListener.onConnectFail(e, "TimeoutException");
                }
                if (e instanceof IOException) {
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onConnectFail(e, "服务器没有开启");
                    }
                    insideDisconnect();
                    e.printStackTrace();
                }
            }
//            catch (IOException e) {//源
//                if (mOnConnectListener != null) {
//                    mOnConnectListener.onConnectFail(e, "服务器没有开启");
//                }
//                insideDisconnect();
//                e.printStackTrace();
//            }
        }
    }

//    public static byte[] readInputStream(InputStream ins) {//源
    public static String readInputStream(InputStream ins) {
        //能完整的读完IO流。尽量不要用字符流，一行行的读取，需要对方发送换行符\r \n符号，才能读取。
        BufferedInputStream bis = new BufferedInputStream(ins);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[128];
            int n = -1;
            while ((n = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        return bos.toByteArray();
        return bos.toByteArray().toString();
    }


    class ClientReceiverRunnable implements Runnable {

        private Socket socket;

        public ClientReceiverRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            //读取服务器端数据（服务端发送数据过来时，需要data末加换行符 \r \n，表示数据的结束，否则客户端会一直在等待接收，代码问题？）
//            try{//字符流方式 ，一行行的读取，需要对方发送换行符\r \n符号，才能读取。
//                BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String line = "";
//                String buffer="";
//                while ((line = bff.readLine()) != null && !buffer.endsWith("END")) {
//                    buffer = line + buffer;
//                }
//                LogWawa.i("recMsg==>>" + buffer.toString());
//                mOnConnectListener.onMessage(buffer);
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }

//            try {//测试
//                byte[] buf = new byte[1024];
//                int len = 0;
//                InputStream input = socket.getInputStream();
//                while((len = input.read(buf))!=-1){
//                    System.out.println(new String(buf,0,len));
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }

            try {
                String message = readInputStream(socket.getInputStream());
                LogWawa.i("recMsg==>>" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            System.out.println(reader.readLine());
//            这个是按行读取必须有换行符 '\n'
//
//            InputStream input = socket.getInputStream();
//            while((len = input.read(buf))!=-1){
//                System.out.println(new String(buf,0,len));
//            }
//            这个是字节流的方式，显然能读，读到缓冲区继续循环 直到通道中的流没有了。

            boolean isContinue = true;
            while (isContinue) {    LogWawa.i("0000000000000");
                try {
                    if (socket != null && !socket.isClosed() && socket.isConnected() && !socket.isInputShutdown()) {
                        String clientReceiverMessage;
                        LogWawa.i("111111111111111111");
                        if ((clientReceiverMessage = mClientIn.readLine()) != null) {//服务器断开连接时，读取的内容为null，这与发送消息为“”不同
                            // 客户端接收到消息
                            if (mOnConnectListener != null) {
                                mOnConnectListener.onMessage(clientReceiverMessage);
                                LogWawa.i("recMsg==>>" + clientReceiverMessage.toString());
                            }
                        } else {LogWawa.i("2222222222222222");
                            //服务端断开连接
                            isContinue = false;
                            if (mOnConnectListener != null) {
                                mOnConnectListener.onConnectError(new RuntimeException("服务器断开连接"), "服务器断开连接");
                            }
                            insideDisconnect();
                        }
                    }
                } catch (Exception e) {
                    isContinue = false;
                    e.printStackTrace();
                    if (mOnConnectListener != null) {
                        mOnConnectListener.onConnectError(e, "客户端连接超时");
                    }
                }
            }
            try {//关闭各种输入输出流
                if (mClientIn != null) {
                    mClientIn.close();
                }
                if (mClientOut != null) {
                    mClientOut.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //每隔一段时间  发心跳包检测服务端是否已经断开连接
    class isConnectedRunnable implements Runnable {

        private Socket socket;
        public isConnectedRunnable(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    //心跳包只是用来检测socket的链接状态，并不会作为socket链接的通信内容
                    socket.sendUrgentData(0xFF); // 发送心跳包 检测服务端是否已经断开连接
                    System.out.println("socket=======>>目前处于链接状态！");
                    Thread.sleep(3 * 1000);//线程睡眠N秒
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("socket=======>>服务器断开！！");
            }
        }
    }


    private void clientSendMessage() {
        if (mClientSendMessage == null || mClientSendMessage.length() == 0) {
            if (mOnConnectListener != null) {
                mOnConnectListener.onConnectError(new RuntimeException("请输入内容"), "请输入内容");
            }
            return;
        }
        if (mClientSocket != null && mClientSocket.isConnected() && !mClientSocket.isOutputShutdown()
            && mClientOut != null) {
            try{
                mClientOut.println(mClientSendMessage);
                LogWawa.i("========>>SendMessage！！");

//                new Handler().postDelayed(new Runnable(){
//                    public void run() {
//                        mClientOut.println(mClientSendMessage);
//                    }
//                }, 500);   //延时2秒

//                Thread thread = new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        mClientOut.println(mClientSendMessage);
//                    }
//                };
//                thread.start();

            }catch (Exception e){
                e.printStackTrace();
                LogWawa.i(e.toString());
                //服务端已经断开连接，需要重新连接？
            }

        } else {
            if (mOnConnectListener != null) {
                mOnConnectListener.onConnectError(new RuntimeException("客户端已经断开连接"), "客户端已经断开连接");
            }
        }
    }

}
