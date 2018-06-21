package com.guxingdongli.yizhangguan.util;

import com.guxingdongli.yizhangguan.R;
import com.guxingdongli.yizhangguan.controller.YiZhangGuanApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by jackmask on 2018/3/12.
 */

public class OkhttpManager {


    static private OkhttpManager mOkhttpManager=null;
    private InputStream mTrustrCertificate;
    static public OkhttpManager getInstance()
    {
        if(mOkhttpManager==null)
        {
            mOkhttpManager=new OkhttpManager();
        }
        return mOkhttpManager;
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }


    public void setTrustrCertificates(InputStream in)
    {

        mTrustrCertificate=in;
    }

    public InputStream getTrustrCertificates()
    {
        return mTrustrCertificate;
    }

//    private  SSLSocketFactory getSSLSocketFactory(InputStream... certificates) {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null);
//            int index = 0;
//            for (InputStream certificate : certificates) {
//                String certificateAlias = Integer.toString(index++);
//                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
//
//                try {
//                    if (certificate != null)
//                        certificate.close();
//                } catch (IOException e) {
//                }
//            }
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//            return sslContext.getSocketFactory();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public OkHttpClient build()
    {
        OkHttpClient okHttpClient=null;
        if(getTrustrCertificates()!=null)
        {
//            X509TrustManager trustManager;
//            SSLSocketFactory sslSocketFactory;
//            try {
//                trustManager = trustManagerForCertificates(getTrustrCertificates());
//                SSLContext sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, new TrustManager[] { trustManager }, null);
//                sslSocketFactory = sslContext.getSocketFactory();
//            } catch (GeneralSecurityException e) {
//                throw new RuntimeException(e);
//            }
//            okHttpClient=new OkHttpClient.Builder()
//                    .hostnameVerifier(new HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            try {
//                                String peerHost = session.getPeerHost(); //服务器返回的主机名
//                                String str_new = "twd";//验证证书
//                                X509Certificate[] peerCertificates = (X509Certificate[]) session.getPeerCertificates();
//                                for (X509Certificate certificate : peerCertificates) {
//                                    X500Principal subjectX500Principal = certificate
//                                            .getSubjectX500Principal();
//                                    String name = subjectX500Principal.getName();
//                                    String[] split = name.split(",");
//                                    for (String str : split) {
//                                        if (str.startsWith("CN")) {//证书绑定的域名或者ip
//                                            if (str.contains(str_new)) {
//                                                return true;
//                                            }
//                                        }
//                                    }
//                                }
//                            } catch (SSLPeerUnverifiedException e1) {
//                                // TODO Auto-generated catch block
//                                e1.printStackTrace();
//                            }
//                            return false;
//                        }
//                    })
//                    .sslSocketFactory(sslSocketFactory, trustManager)
//                    .build();

            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                String certificateAlias = Integer.toString(0);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.
                        generateCertificate(YiZhangGuanApplication.getInstance().getApplicationContext().getResources().openRawResource(R.raw.yizhangguan)));
                SSLContext sslContext = SSLContext.getInstance("TLS");
                final TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                sslContext.init
                        (
                                null,
                                trustManagerFactory.getTrustManagers(),
                                new SecureRandom()
                        );
                okHttpClient=new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory()).hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).build();
            } catch (CertificateException pE) {
                pE.printStackTrace();
            } catch (NoSuchAlgorithmException pE) {
                pE.printStackTrace();
            } catch (KeyStoreException pE) {
                pE.printStackTrace();
            } catch (IOException pE) {
                pE.printStackTrace();
            } catch (KeyManagementException pE) {
                pE.printStackTrace();
            }


        }
        else
        {
            okHttpClient=new OkHttpClient.Builder()
                    .build();
        }
        return okHttpClient;
    }



}
