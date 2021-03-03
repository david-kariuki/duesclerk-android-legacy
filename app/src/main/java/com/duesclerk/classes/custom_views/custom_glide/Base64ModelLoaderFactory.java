package com.duesclerk.classes.custom_views.custom_glide;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class Base64ModelLoaderFactory implements ModelLoaderFactory<String, ByteBuffer> {

    @Override
    public @NotNull ModelLoader<String, ByteBuffer> build(@NotNull MultiModelLoaderFactory multiFactory) {
        return new Base64ModelLoader();
    }

    @Override
    public void teardown() {
        // Do nothing.
    }
}
