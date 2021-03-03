package com.duesclerk.classes.custom_views.custom_glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import okhttp3.OkHttpClient;

@GlideModule
@Excludes(OkHttpLibraryGlideModule.class) // initialize OkHttp manually
public final class CustomGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);

        OkHttpClient okHttpClient = new OkHttpClient();
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(okHttpClient));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NotNull Context context, GlideBuilder builder) {
        // Glide default Bitmap Format is set to RGB_565 since it
        // consumed just 50% memory footprint compared to ARGB_8888.
        // Increase memory usage for quality with:

        //Set Default Custom_Request Policy
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888));

        final GlideExecutor.UncaughtThrowableStrategy uncaughtThrowableStrategy = t -> {
        };

        //builder.setDiskCacheExecutor(new DiskCacheExecutor(uncaughtThrowableStrategy));
        //builder.setResizeExecutor(new SourceExecutor(uncaughtThrowableStrategy));

        //Set Log Level
        //builder.setLogLevel(Log.DEBUG);
        builder.setLogLevel(Log.ERROR);


        // Set Memory Custom_Cache Size

        //Overriding Memory Custom_Cache Size
        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));

        //Using MemorySizeCalculator:
        //MemorySizeCalculator calculator = new MemorySizeCalculator
        // .Builder(context).setMemoryCacheScreens(2).build();
        //builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));

        //Custom Implementation
        //builder.setMemoryCache(new YourAppMemoryCacheImpl());

        // Set BitmapPool Size

        //Overriding Pool Size
        int bitmapPoolSizeBytes = 1024 * 1024 * 30; // 30mb
        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));

        //Using MemorySizeCalculator:
        //MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
        // .setBitmapPoolScreens(3).build();
        //builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize()));

        //Custom Implementation
        //builder.setBitmapPool(new YourAppBitmapPoolImpl());

        // Set Disk Custom_Cache Size

        //Overriding Disk Custom_Cache Size
        //This Changes the location to external storage if the media they display is public
        // (obtained from websites without authentication, search engines etc):
        int diskCacheSizeBytes = 1024 * 1024 * 100; // 100 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));

        //This changes the size of the disk, for either the internal or external disk caches:
        //int diskCacheSizeBytes = 1024 * 1024 * 100; // 100 MB
        //builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));

        //This changes the name of the folder the cache is placed in within external or internal
        // storage:
        //int diskCacheSizeBytes = 1024 * 1024 * 100; // 100 MB
        //builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "cacheFolderName",
        // diskCacheSizeBytes));

        //Custom Implementation
        /*builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                return new YourAppCustomDiskCache();
            }
        });*/
    }

}
