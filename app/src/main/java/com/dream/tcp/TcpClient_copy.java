//package com.dream.tcp;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
//
//
///**
// * Author:      SuSong
// * Email:       751971697@qq.com | susong0618@163.com
// * GitHub:      https://github.com/susong0618
// * Date:        16/4/5 下午11:00
// * Description: TcpDemo
// */
//public class TcpClient_copy {
//    private static final String    TAG        = "TcpClient";
//    private static TcpClient_copy mTcpClient = new TcpClient_copy();
//    private ClientSocketThread mClientSocketThread;
//    private OnConnectListener  mOnConnectListener;
//
//    public static TcpClient_copy getInstance() {
//        return mTcpClient;
//    }
//
//
//    public void setOnConnectListener(OnConnectListener onConnectListener) {
//        mOnConnectListener = onConnectListener;
//    }
//
//    public interface OnConnectListener {
//        /**
//         * @param inetAddress  服务端地址
//         * @param localAddress 客户端地址
//         */
//        void onConnectSuccess(String inetAddress, String localAddress);
//
//        void onConnectFail(Throwable e, String message);
//
//        void onConnectError(Throwable e, String message);
//
//        void onMessage(String message);
//    }
//
//    public void setIp(String ip) {
//        mClientIp = ip;
//    }
//
//    public void setPort(int port) {
//        mClientPort = port;
//    }
//
//    public void connect() {
//        if (mOnConnectListener == null) {
//            throw new RuntimeException("请设置OnConnectListener");
//        }
//        if (mClientIp == null || mClientIp.length() == 0 || mClientPort == 0) {
//            mOnConnectListener.onConnectFail(new RuntimeException("请设置ip与port"), "请设置ip与port");
//            return;
//        }
//        if (mClientSocketThread == null) {
//            mClientSocketThread = new ClientSocketThread();
//            mClientSocketThread.start();
//        } else {
//            mOnConnectListener.onConnectError(new RuntimeException("已经建立连接"), "已经建立连接");
//        }
//    }
//
//    public void connect(String ip, int port, OnConnectListener onConnectListener) {
//        mOnConnectListener = onConnectListener;
//        mClientIp = ip;
//        mClientPort = port;
//        connect();
//    }
//
//    public void disconnect() {
//        if (mOnConnectListener == null) {
//            throw new RuntimeException("请设置OnConnectListener");
//        }
//        if (mClientSocket != null) {
//            try {
//                mClientSocket.close();
//                mClientIn.close();
//                mClientOut.close();
//                mClientOut = null;
//                mClientIn = null;
//                mClientSocket = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            mOnConnectListener.onConnectError(new RuntimeException("已经断开连接"), "已经断开连接");
//        }
//        if (mClientSocketThread != null) {
//            mClientSocketThread = null;
//        }
//    }
//
//    public void sendMessage(String message) {
//        if (mOnConnectListener == null) {
//            throw new RuntimeException("请设置OnConnectListener");
//        }
//        mClientSendMessage = message;
//        clientSendMessage();
//    }
//
//    private void insideDisconnect() {
//        LogWawa.i("insideDisconnect!!!");
//        if (mClientSocket != null) {
//            try {
//                mClientSocket.close();
//                mClientIn.close();
//                mClientOut.close();
//                mClientOut = null;
//                mClientIn = null;
//                mClientSocket = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (mClientSocketThread != null) {
//            mClientSocketThread = null;
//        }
//    }
//    //=================================================================================
//    // Client
//    //=================================================================================
//
//    private String         mClientIp;
//    private int            mClientPort;
//    private Socket         mClientSocket;
//    private BufferedReader mClientIn;
//    private PrintWriter    mClientOut;
//    private String         mClientSendMessage;
//
//    class ClientSocketThread extends Thread {
//
//        @Override
//        public void run() {
//            try {
//                mClientSocket = new Socket(mClientIp, mClientPort);
//                mClientIn = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
//                mClientOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream())), true);
//                ClientReceiverRunnable runnable = new ClientReceiverRunnable(mClientSocket);
//                new Thread(runnable).start();
//                if (mOnConnectListener != null) {
//                    mOnConnectListener.onConnectSuccess(mClientSocket.getInetAddress().getHostAddress(), mClientSocket.getLocalAddress().getHostAddress());
//                }
//            } catch (Exception e){
//                if (e instanceof SocketTimeoutException) {
//                    mOnConnectListener.onConnectFail(e, "TimeoutException");
//                }
//                if (e instanceof IOException) {
//                    if (mOnConnectListener != null) {
//                        mOnConnectListener.onConnectFail(e, "服务器没有开启");
//                    }
//                    insideDisconnect();
//                    e.printStackTrace();
//                }
//            }
////            catch (IOException e) {//源
////                if (mOnConnectListener != null) {
////                    mOnConnectListener.onConnectFail(e, "服务器没有开启");
////                }
////                insideDisconnect();
////                e.printStackTrace();
////            }
//        }
//    }
//
//    class ClientReceiverRunnable implements Runnable {
//
//        private Socket socket;
//
//        public ClientReceiverRunnable(Socket socket) {
//            this.socket = socket;
//        }
//
//        @Override
//        public void run() {
//            try{
//                //读取服务器端数据
//                BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String line = "";
//                String buffer="";
//                while ((line = bff.readLine()) != null && !buffer.endsWith("END")) {
//                    buffer = line + buffer;
//                }
//
//
//                LogWawa.i("recMsg==>>" + buffer.toString());
//                mOnConnectListener.onMessage(buffer);
//
////                String line = "";
////                String buffer="";
////                while ((line = mClientIn.readLine()) != null && !buffer.endsWith("END")) {
////                    buffer = line + buffer;
////                }
////                LogWawa.i("recMsg==>>" + buffer.toString());
////                mOnConnectListener.onMessage(buffer);
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//
//
////            boolean isContinue = true;
////            while (isContinue) {LogWawa.i("00000000000000");
////                try {
////                    if (socket != null && !socket.isClosed() && socket.isConnected() && !socket.isInputShutdown()) {
////                        String clientReceiverMessage;
////                        LogWawa.i("111111111111111111");
////                        if ((clientReceiverMessage = mClientIn.readLine()) != null) {//服务器断开连接时，读取的内容为null，这与发送消息为“”不同
////                            // 客户端接收到消息
////                            if (mOnConnectListener != null) {
////                                mOnConnectListener.onMessage(clientReceiverMessage);
////                                LogWawa.i("recMsg==>>" + clientReceiverMessage.toString());
////                            }
////                        } else {LogWawa.i("2222222222222222");
////                            //服务端断开连接
////                            isContinue = false;
////                            if (mOnConnectListener != null) {
////                                mOnConnectListener.onConnectError(new RuntimeException("服务器断开连接"), "服务器断开连接");
////                            }
////                            insideDisconnect();
////                        }
////                    }
////                } catch (Exception e) {
////                    isContinue = false;
////                    e.printStackTrace();
////                    if (mOnConnectListener != null) {
////                        mOnConnectListener.onConnectError(e, "客户端连接超时");
////                    }
////                }
////            }
////            try {//关闭各种输入输出流
////                if (mClientIn != null) {
////                    mClientIn.close();
////                }
////                if (mClientOut != null) {
////                    mClientOut.close();
////                }
////                if (socket != null) {
////                    socket.close();
////                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//        }
//    }
//
//    private void clientSendMessage() {
//        if (mClientSendMessage == null || mClientSendMessage.length() == 0) {
//            if (mOnConnectListener != null) {
//                mOnConnectListener.onConnectError(new RuntimeException("请输入内容"), "请输入内容");
//            }
//            return;
//        }
//        if (mClientSocket != null && mClientSocket.isConnected() && !mClientSocket.isOutputShutdown()
//            && mClientOut != null) {
//            try{
////                mClientOut.println(mClientSendMessage);
//
////                new Handler().postDelayed(new Runnable(){
////                    public void run() {
////                        mClientOut.println(mClientSendMessage);
////                    }
////                }, 500);   //延时2秒
//
//                Thread thread = new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        mClientOut.println(mClientSendMessage);
//                    }
//                };
//                thread.start();
//
//            }catch (Exception e){
//                e.printStackTrace();
//                LogWawa.i(e.toString());
//            }
//
//        } else {
//            if (mOnConnectListener != null) {
//                mOnConnectListener.onConnectError(new RuntimeException("客户端已经断开连接"), "客户端已经断开连接");
//            }
//        }
//    }
//
//}
