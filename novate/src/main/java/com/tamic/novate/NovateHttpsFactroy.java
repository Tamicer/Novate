/*
 *    Copyright (C) 2016 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.tamic.novate;

import android.content.Context;

import com.tamic.novate.util.LogWraper;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * NovateHttpsFactroy
 * Created by Tamic on 2016-06-27.
 * {@link // http://developer.android.com/training/articles/security-ssl.html#Concepts}
 */
public class NovateHttpsFactroy {

    private static HostnameVerifier TRUSTED_VERIFIER;

    /**
     * set SSLSocketFactory
     * {@link HostnameVerifier}
     */
    protected static SSLSocketFactory getSSLSocketFactory(Context context, int[] certificates) {

        if (context == null) {
            throw new NullPointerException("context == null");
        }

        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            for (int i = 0; i < certificates.length; i++) {
                InputStream certificate = context.getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(certificate));

                if (certificate != null) {
                    certificate.close();
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * set HostnameVerifier
     * {@link HostnameVerifier}
     */
    protected static HostnameVerifier getHostnameVerifier(final String[] hostUrls) {

        HostnameVerifier TRUSTED_VERIFIER = new HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                boolean ret = false;
                for (String host : hostUrls) {
                    if (host.equalsIgnoreCase(hostname)) {
                        ret = true;
                    }
                }
                return ret;
            }
        };

        return TRUSTED_VERIFIER;
    }

    /**
     * set Pins
     * {@link HostnameVerifier}
     */
    protected static String getPins(Context context, int certificate) {

        InputStream in = context.getResources().openRawResource(certificate);

        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        try {
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return out.toString();

    }

    /**
     * get SSLSocketFactory
     *
     * @param context context
     * @param name    Assets Certificate file name
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory creatSSLSocketFactory(Context context, String name) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        //CertificateFactory
        CertificateFactory certificateFactory;
        InputStream inputStream = null;
        Certificate certificate;
        try {
            inputStream = context.getResources().getAssets().open(name);
        } catch (IOException e) {
            e.printStackTrace();
            LogWraper.e("Novate", "SSLSocketFactory open file error :" + e.toString());
        }
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            certificate = certificateFactory.generateCertificate(inputStream);
            //Create a KeyStore containing our trusted CAs
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry(name, certificate);
            //Create a TrustManager that trusts the CAs in our keyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            //Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            LogWraper.e("Novate", "SSLSocketFactory generate error :" + e.toString());
        }
        return null;
    }

    /**
     * get SkipHostnameVerifier
     * @return
     */
    public static HostnameVerifier creatSkipHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        return hostnameVerifier;
    }


    /**
     * TrustManager
     */
    private static TrustManager[] creatTrustManager() {
                TrustManager[] trustAllCerts = new TrustManager[]{
                               new X509TrustManager() {
                             @Override
                               public void checkClientTrusted(X509Certificate[] chain, String authType) {
                                   }
                                        @Override
                                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                                   }

                                       @Override
                               public X509Certificate[] getAcceptedIssuers() {
                                       return new X509Certificate[]{};
                                   }
                          }
                        };
               return trustAllCerts;
            }
}
