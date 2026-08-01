package com.techno.aiproject.api;

import com.techno.aiproject.utils.Constants;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit plantNetRetrofit;
    private static Retrofit geminiRetrofit;
    private static Retrofit backendRetrofit;

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);

            // Set timeouts for long-running requests (especially Gemini API)
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(3, TimeUnit.MINUTES);  // 180 seconds for Gemini responses

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PlantNetService getPlantNetService() {
        if (plantNetRetrofit == null) {
            plantNetRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.PLANTNET_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build();
        }
        return plantNetRetrofit.create(PlantNetService.class);
    }

    public static GeminiService getGeminiService() {
        if (geminiRetrofit == null) {
            geminiRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.GEMINI_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build();
        }
        return geminiRetrofit.create(GeminiService.class);
    }

    private static Retrofit getBackendRetrofit() {
        if (backendRetrofit == null) {
            backendRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BACKEND_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build();
        }
        return backendRetrofit;
    }

    public static LoginInterface getLoginInterface() {
        return getBackendRetrofit().create(LoginInterface.class);
    }

    public static SubmitDetails getSubmitDetails() {
        return getBackendRetrofit().create(SubmitDetails.class);
    }

    public static CheckPhoneNumber getCheckPhoneNumber() {
        return getBackendRetrofit().create(CheckPhoneNumber.class);
    }

    public static SendPassword getSendPassword() {
        return getBackendRetrofit().create(SendPassword.class);
    }
}
