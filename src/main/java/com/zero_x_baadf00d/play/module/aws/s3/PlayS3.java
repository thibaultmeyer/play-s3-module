/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 - 2020 Thibault Meyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zero_x_baadf00d.play.module.aws.s3;

import com.amazonaws.services.s3.AmazonS3;

/**
 * PlayS3.
 *
 * @author Thibault Meyer
 * @version 17.02.02
 * @since 17.02.02
 */
public final class PlayS3 {

    /**
     * Handle to the instance of Amazon PlayS3 client.
     *
     * @since 17.02.02
     */
    static AmazonS3 amazonS3;

    /**
     * The name of the bucket to use.
     *
     * @since 17.02.02
     */
    static String bucketName;

    /**
     * The public URL to use.
     *
     * @since 17.02.02
     */
    static String publicUrl;

    /**
     * Get the current instance of Amazon PlayS3 client
     *
     * @return The current instance of Amazon PlayS3 client
     * @since 17.02.02
     */
    public static AmazonS3 getAmazonS3() {
        return PlayS3.amazonS3;
    }

    /**
     * Get the current bucket name.
     *
     * @return The current bucket name
     * @since 17.02.02
     */
    public static String getBucketName() {
        return PlayS3.bucketName;
    }

    /**
     * Get the public URL to use.
     *
     * @return The public URL to use
     * @since 17.02.02
     */
    public static String getPublicUrl() {
        return PlayS3.publicUrl;
    }

    /**
     * Check if Amazon PlayS3 module is ready.
     *
     * @return {@code true} if ready
     * @since 17.02.02
     */
    public static boolean isReady() {
        return amazonS3 != null;
    }
}
