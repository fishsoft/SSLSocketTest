package com.morse.socketopenssl;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * author：Morse
 * time：2016/10/26 15:12
 * Descripte：
 */
public class SSLSocket {

    private static final int SERVER_PORT = 50030;//端口号
    private static final String SERVER_IP = "218.206.176.146";//连接IP
    private static final String CLIENT_KET_PASSWORD = "123456";//私钥密码
    private static final String CLIENT_TRUST_PASSWORD = "123456";//信任证书密码
    private static final String CLIENT_AGREEMENT = "TLS";//使用协议
    private static final String CLIENT_KEY_MANAGER = "X509";//密钥管理器
    private static final String CLIENT_TRUST_MANAGER = "X509";//
    private static final String CLIENT_KEY_KEYSTORE = "BKS";//密库，这里用的是BouncyCastle密库
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";//
    private static final String ENCONDING = "utf-8";//字符集

    private javax.net.ssl.SSLSocket sslSocket;
    private CallBack callBack;

    public SSLSocket(Context context, int keyStoreId) {
        init(context, keyStoreId);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    private void init(Context context, int keyStoreId) {
        try {
            //取得SSL的SSLContext实例
            SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
            //取得KeyManagerFactory和TrustManagerFactory的X509密钥管理器实例
            KeyManagerFactory keyManager = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
            //取得BKS密库实例
            KeyStore kks = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
            KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
            //加客户端载证书和私钥,通过读取资源文件的方式读取密钥和信任证书
            kks.load(context.getResources().openRawResource(keyStoreId), CLIENT_KET_PASSWORD.toCharArray());
            tks.load(context.getResources().openRawResource(keyStoreId), CLIENT_TRUST_PASSWORD.toCharArray());
            //初始化密钥管理器
            keyManager.init(kks, CLIENT_KET_PASSWORD.toCharArray());
            trustManager.init(tks);
            //初始化SSLContext
            sslContext.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
            //生成SSLSocket
            sslSocket = (javax.net.ssl.SSLSocket) sslContext.getSocketFactory().createSocket(SERVER_IP, SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream())), true);
            if (null != callBack) {
                callBack.sendResponse(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (null != callBack) {
                callBack.onFailure(e.getMessage());
            }
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    public void read() {
        BufferedReader in = null;
        String str = null;
        try {
            in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            str = new String(in.readLine().getBytes(), ENCONDING);
            if (null != callBack) {
                callBack.readResponse(str);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (null != callBack) {
                callBack.onFailure(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (null != callBack) {
                callBack.onFailure(e.getMessage());
            }
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface CallBack {
        void readResponse(String str);

        void sendResponse(String str);

        void onFailure(String str);
    }

}
