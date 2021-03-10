package com.duesclerk.classes.custom_views.custom_glide;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Loads an {@link InputStream} from a Base 64 encoded String.
 */
public final class Base64ModelLoader implements ModelLoader<String, ByteBuffer> {

    // From: https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs.
    private static final String DATA_URI_PREFIX = "data:";

    @Override
    public @NotNull LoadData<ByteBuffer> buildLoadData(@NotNull String model, int width, int height,
                                                       @NotNull Options options) {
        return new LoadData<>(new ObjectKey(model), new Base64DataFetcher(model));
    }

    @Override
    public boolean handles(String model) {
        return model.startsWith(DATA_URI_PREFIX);
    }
}
