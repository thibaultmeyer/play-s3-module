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
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.typesafe.config.Config;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@code AmazonS3Module}.
 *
 * @author Thibault Meyer
 * @version 17.07.05
 * @since 16.03.13
 */
@Singleton
public final class AmazonS3ModuleInitializer {

    /**
     * Create a simple instance of {@code S3Module}.
     *
     * @param lifecycle     The application life cycle
     * @param configuration The application configuration
     * @since 16.03.13
     */
    @Inject
    public AmazonS3ModuleInitializer(final ApplicationLifecycle lifecycle, final Config configuration) {
        final String accessKey;
        final String secretKey;

        if (configuration.hasPath("aws.s3.authKey")) {
            accessKey = configuration.getString("aws.s3.authKey");
        } else {
            accessKey = configuration.getString("aws.authKey");
        }
        if (configuration.hasPath("aws.s3.authSecret")) {
            secretKey = configuration.getString("aws.s3.authSecret");
        } else {
            secretKey = configuration.getString("aws.authSecret");
        }
        final String endPoint = configuration.getString("aws.s3.endPoint");
        final String signingRegion = configuration.getString("aws.s3.signingRegion");

        final boolean withPathStyle = configuration.hasPath("aws.s3.withPathStyle")
            && configuration.getBoolean("aws.s3.withPathStyle");
        final boolean withChunkedEncodingDisabled = configuration.hasPath("aws.s3.disableChunkedEncoding")
            && configuration.getBoolean("aws.s3.disableChunkedEncoding");

        PlayS3.bucketName = configuration.getString("aws.s3.bucketName");
        PlayS3.publicUrl = configuration.hasPath("aws.s3.publicUrl") ? configuration.getString("aws.s3.publicUrl") : "/";
        if (!PlayS3.publicUrl.endsWith("/")) {
            PlayS3.publicUrl += "/";
        }

        if (accessKey == null || secretKey == null || PlayS3.bucketName == null) {
            throw new RuntimeException("S3Module is not properly configured");
        }

        PlayS3.amazonS3 = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return new BasicAWSCredentials(accessKey, secretKey);
                }

                @Override
                public void refresh() {
                    // Not used with basic AWS credentials
                }
            })
            .withPathStyleAccessEnabled(withPathStyle)
            .withChunkedEncodingDisabled(withChunkedEncodingDisabled)
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(endPoint, signingRegion)
            )
            .build();
        try {
            PlayS3.amazonS3.createBucket(PlayS3.bucketName);
        } catch (final AmazonS3Exception ex) {
            if (ex.getErrorCode().compareTo("BucketAlreadyOwnedByYou") != 0
                && ex.getErrorCode().compareTo("AccessDenied") != 0) {
                throw ex;
            }
        } finally {
            Logger.info("Using PlayS3 Bucket: " + PlayS3.bucketName);
        }

        lifecycle.addStopHook(() -> CompletableFuture.completedFuture(null));
    }
}
