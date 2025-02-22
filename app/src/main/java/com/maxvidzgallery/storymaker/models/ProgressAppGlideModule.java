package com.maxvidzgallery.storymaker.models;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

@GlideModule
public class ProgressAppGlideModule extends AppGlideModule {

    private static class DispatchingProgressListener implements ResponseProgressListener {
        private static final Map<String, UIonProgressListener> LISTENERS = new HashMap();
        private static final Map<String, Long> PROGRESSES = new HashMap();
        private final Handler handler = new Handler(Looper.getMainLooper());

        DispatchingProgressListener() {
        }

        static void forget(String str) {
            LISTENERS.remove(str);
            PROGRESSES.remove(str);
        }

        static void expect(String str, UIonProgressListener uIonProgressListener) {
            LISTENERS.put(str, uIonProgressListener);
        }

        public void update(HttpUrl httpUrl, long j, long j2) {
            String httpUrl2 = httpUrl.toString();
            UIonProgressListener uIonProgressListener = (UIonProgressListener) LISTENERS.get(httpUrl2);
            if (uIonProgressListener != null) {
                if (j2 <= j) {
                    forget(httpUrl2);
                }
                if (needsDispatch(httpUrl2, j, j2, uIonProgressListener.getGranualityPercentage())) {
                    Handler handler2 = this.handler;
                    final UIonProgressListener uIonProgressListener2 = uIonProgressListener;
                    final long j3 = j;
                    final long j4 = j2;
                    Runnable runnable = new Runnable() {
                        public void run() {
                            uIonProgressListener2.onProgress(j3, j4);
                        }
                    };
                    handler2.post(runnable);
                }
            }
        }

        private boolean needsDispatch(String str, long j, long j2, float f) {
            if (!(f == 0.0f || j == 0 || j2 == j)) {
                long j3 = (long) (((((float) j) * 100.0f) / ((float) j2)) / f);
                Long l = (Long) PROGRESSES.get(str);
                if (l != null && j3 == l.longValue()) {
                    return false;
                }
                PROGRESSES.put(str, Long.valueOf(j3));
            }
            return true;
        }
    }

    private static class OkHttpProgressResponseBody extends ResponseBody {

        private BufferedSource bufferedSource;
        private final ResponseProgressListener progressListener;
        private final ResponseBody responseBody;
        private final HttpUrl url;

        OkHttpProgressResponseBody(HttpUrl httpUrl, ResponseBody responseBody2, ResponseProgressListener responseProgressListener) {
            this.url = httpUrl;
            this.responseBody = responseBody2;
            this.progressListener = responseProgressListener;
        }

        public MediaType contentType() {
            return this.responseBody.contentType();
        }

        public long contentLength() {
            return this.responseBody.contentLength();
        }

        public BufferedSource source() {
            if (this.bufferedSource == null) {
                this.bufferedSource = Okio.buffer(source(this.responseBody.source()));
            }
            return this.bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0;

                public long read(Buffer buffer, long j) throws IOException {
                    long read = super.read(buffer, j);
                    long contentLength = OkHttpProgressResponseBody.this.responseBody.contentLength();
                    if (read == -1) {
                        this.totalBytesRead = contentLength;
                    } else {
                        this.totalBytesRead += read;
                    }
                    OkHttpProgressResponseBody.this.progressListener.update(OkHttpProgressResponseBody.this.url, this.totalBytesRead, contentLength);
                    return read;
                }
            };
        }
    }

    private interface ResponseProgressListener {
        void update(HttpUrl httpUrl, long j, long j2);
    }

    public interface UIonProgressListener {
        float getGranualityPercentage();

        void onProgress(long j, long j2);
    }

    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
    }

    public static void forget(String str) {
        DispatchingProgressListener.forget(str);
    }

    public static void expect(String str, UIonProgressListener uIonProgressListener) {
        DispatchingProgressListener.expect(str, uIonProgressListener);
    }
}
