package com.odeyalo.sonata.connect.support;

import org.jetbrains.annotations.NotNull;

/**
 * Support interface that implement methods from {@link CharSequence}
 */
public interface DefaultCharSequence extends CharSequence {

    @Override
    default int length() {
        return toString().length();
    }

    @Override
    default char charAt(int index) {
        return toString().charAt(index);
    }

    @NotNull
    @Override
    default CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }
}
