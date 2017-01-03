/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 - 2017 Thibault Meyer
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@code AmazonS3Module}.
 *
 * @author Thibault Meyer
 * @version 16.03.16
 * @since 16.03.13
 */
@Singleton
public class AmazonS3ModuleImpl implements AmazonS3Module {

    /**
     * @since 16.03.13
     */
    private static final String AWS_S3_BUCKET = "aws.s3.bucket";

    /**
     * @since 16.03.13
     */
    private static final String AWS_ENDPOINT = "aws.s3.endpoint";

    /**
     * @since 16.03.13
     */
    private static final String AWS_WITHPATHSTYLE = "aws.s3.pathstyle";

    /**
     * @since 16.03.13
     */
    private static final String AWS_ACCESS_KEY = "aws.access.key";

    /**
     * @since 16.03.13
     */
    private static final String AWS_SECRET_KEY = "aws.secret.key";

    /**
     * Handle on the S3 API client.
     *
     * @since 16.03.13
     */
    private final AmazonS3Client amazonS3;

    /**
     * Name of the S3 bucket to use.
     *
     * @since 16.03.13
     */
    private final String s3Bucket;

    /**
     * Create a simple instance of {@code S3Module}.
     *
     * @param lifecycle     The application life cycle
     * @param configuration The application configuration
     * @since 16.03.13
     */
    @Inject
    public AmazonS3ModuleImpl(final ApplicationLifecycle lifecycle, final Configuration configuration) {
        final String accessKey = configuration.getString(AmazonS3ModuleImpl.AWS_ACCESS_KEY);
        final String secretKey = configuration.getString(AmazonS3ModuleImpl.AWS_SECRET_KEY);
        this.s3Bucket = configuration.getString(AmazonS3ModuleImpl.AWS_S3_BUCKET);
        if ((accessKey != null) && (secretKey != null) && (this.s3Bucket != null)) {
            final AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            this.amazonS3 = new AmazonS3Client(awsCredentials);
            this.amazonS3.setEndpoint(configuration.getString(AmazonS3ModuleImpl.AWS_ENDPOINT));
            if (configuration.getBoolean(AmazonS3ModuleImpl.AWS_WITHPATHSTYLE, false)) {
                this.amazonS3.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
            }
            try {
                this.amazonS3.createBucket(this.s3Bucket);
            } catch (AmazonS3Exception e) {
                if (e.getErrorCode().compareTo("BucketAlreadyOwnedByYou") != 0 && e.getErrorCode().compareTo("AccessDenied") != 0) {
                    throw e;
                }
            } finally {
                Logger.info("Using S3 Bucket: " + this.s3Bucket);
            }
        } else {
            throw new RuntimeException("S3Module is not properly configured");
        }
        lifecycle.addStopHook(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public AmazonS3Client getService() {
        return this.amazonS3;
    }

    @Override
    public String getBucketName() {
        return this.s3Bucket;
    }
}
