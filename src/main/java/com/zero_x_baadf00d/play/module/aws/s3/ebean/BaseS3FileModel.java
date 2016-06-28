/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thibault Meyer
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
package com.zero_x_baadf00d.play.module.aws.s3.ebean;

import com.amazonaws.services.s3.model.*;
import com.avaje.ebean.Model;
import com.fasterxml.uuid.Generators;
import com.zero_x_baadf00d.play.module.aws.s3.AmazonS3Module;
import play.Logger;
import play.Play;

import javax.persistence.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * This abstract class provides all the necessary foundations for
 * the implementation of a model using the Amazon S3 plugin.
 *
 * @author Thibault Meyer
 * @version 16.03.19
 * @since 16.03.13
 */
@MappedSuperclass
public abstract class BaseS3FileModel extends Model implements Cloneable {

    /**
     * The unique ID of the S3 file.
     *
     * @since 16.03.13
     */
    @Id
    @Column(name = "id")
    protected UUID id;

    /**
     * The human readable name of the S3 file.
     *
     * @since 16.03.13
     */
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50)")
    protected String name;

    /**
     * The content type of the S3 file.
     *
     * @since 16.03.13
     */
    @Column(name = "content_type", nullable = false, columnDefinition = "VARCHAR(20)")
    protected String contentType;

    /**
     * Temporary object data. Used to upload
     * object on S3.
     *
     * @since 16.03.13
     */
    @Transient
    protected InputStream objectData;

    /**
     * Is this file private or not. Private file can only be accessed
     * by this application.
     *
     * @since 16.03.13
     */
    @Column(name = "is_private", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    protected boolean isPrivate;

    /**
     * Subdirectory on the bucket where this file is located.
     *
     * @since 16.03.13
     */
    @Column(name = "sub_directory", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
    protected String subDirectory;

    /**
     * Name of the bucket where the file is stored in.
     *
     * @since 16.03.13
     */
    @Column(name = "bucket")
    protected String bucket;

    /**
     * The S3 module instance.
     *
     * @see AmazonS3Module
     * @since 16.03.13
     */
    @Transient
    protected AmazonS3Module s3module;

    /**
     * Get the ID of this {@code S3File} entry.
     *
     * @return The ID
     * @see UUID
     * @since 16.03.13
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Get the ID of this {@code S3File} entry as string.
     *
     * @return The ID as string
     * @since 16.03.13
     */
    public String getIdAsString() {
        return this.id.toString();
    }

    /**
     * Get the filename.
     *
     * @return The filename
     * @since 16.03.13
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the filename.
     *
     * @param name The filename to use
     * @since 16.03.13
     */
    public void setName(final String name) {
        if (this.id == null) {
            this.name = name.trim();
        }
    }

    /**
     * Get the content type (ie: image/png).
     *
     * @return The content type
     * @since 16.03.13
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Set the content type (ie: image/png).
     *
     * @param contentType The content type of the file
     * @since 16.03.13
     */
    public void setContentType(final String contentType) {
        if (this.id == null) {
            this.contentType = contentType.trim();
        }
    }

    /**
     * Is this file private?
     *
     * @return {@code true} if private, otherwise, {@code false}
     * @since 16.03.13
     */
    public boolean isPrivate() {
        return this.isPrivate;
    }

    /**
     * Set if this file private or not.
     *
     * @param aPrivate {@code true} if private, otherwise, {@code false}
     * @since 16.03.13
     */
    public void setPrivate(final boolean aPrivate) {
        if (this.id == null) {
            this.isPrivate = aPrivate;
        }
    }

    /**
     * Get the subdirectory where is located the file.
     *
     * @return The subdirectory where the file is located
     * @since 16.03.13
     */
    public String getSubDirectory() {
        return this.subDirectory;
    }

    /**
     * Set the subdirectory where the file will be saved.
     *
     * @param subDirectory The subdirectory to use
     * @since 16.03.13
     */
    public void setSubDirectory(final String subDirectory) {
        if (this.id == null) {
            this.subDirectory = subDirectory.trim();
        }
    }

    /**
     * Set the object to send to S3.
     *
     * @param file The file to upload
     * @throws FileNotFoundException If the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.
     * @since 16.03.13
     */
    public void setObject(final File file) throws FileNotFoundException {
        if (this.id == null) {
            if (this.objectData != null) {
                try {
                    this.objectData.close();
                } catch (IOException ignore) {
                }
            }
            this.objectData = new FileInputStream(file);
        }
    }

    /**
     * Set the object to send to S3.
     *
     * @param inputStream The data to upload
     * @since 16.03.13
     */
    public void setObject(final InputStream inputStream) {
        if (this.id == null) {
            if (this.objectData != null) {
                try {
                    this.objectData.close();
                } catch (IOException ignore) {
                }
            }
            this.objectData = inputStream;
        }
    }

    /**
     * Get the base URL to access S3 uploaded file. This value can be
     * set on application.conf file.
     *
     * @return The public base URL
     * @since 16.03.13
     */
    protected String getBasePublicUrl() {
        String basePublicUrl = Play.application().configuration().getString("aws.s3.puburl", "/");
        if (!basePublicUrl.endsWith("/")) {
            basePublicUrl += "/";
        }
        return basePublicUrl;
    }

    /**
     * Get the public URL of this S3 file.
     *
     * @return The public URL of this S3 file
     * @throws MalformedURLException If URL is malformed (check application.conf)
     * @since 16.03.13
     */
    public URL getUrl() throws MalformedURLException {
        return new URL(this.getBasePublicUrl() + this.bucket + "/" + this.getActualFileName());
    }

    /**
     * Get the public URL of this S3 file as string.
     *
     * @return The public URL of this S3 file, otherwise, null
     * @since 16.03.13
     */
    public String getUrlAsString() {
        if (id.toString().isEmpty()) {
            return null;
        }
        try {
            return new URL(this.getBasePublicUrl() + this.bucket + "/" + this.getActualFileName()).toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Get the actual file name.
     *
     * @return The actual file name
     * @since 16.03.13
     */
    protected String getActualFileName() {
        if (this.subDirectory == null || this.subDirectory.isEmpty()) {
            return String.format("%s", this.id);
        }
        return String.format("%s/%s", this.subDirectory, this.id);
    }

    /**
     * Save the current object. The file will be uploaded to S3 bucket.
     *
     * @since 16.03.13
     */
    @Override
    public void save() {
        if (this.id == null) {
            this.id = Generators.timeBasedGenerator().generate();
        }
        if (this.s3module == null) {
            this.s3module = Play.application().injector().instanceOf(AmazonS3Module.class);
        }
        if (this.s3module == null) {
            Logger.error("Could not save S3 file because amazonS3 variable is null");
            throw new RuntimeException("Could not save");
        } else {
            this.bucket = this.s3module.getBucketName();
            if (this.subDirectory == null) {
                this.subDirectory = "";
            }
            this.subDirectory = this.subDirectory.trim();

            // Set cache control and server side encryption
            final ObjectMetadata objMetaData = new ObjectMetadata();
            objMetaData.setContentType(this.contentType);
            objMetaData.setCacheControl("max-age=315360000, public");
            objMetaData.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            try {
                objMetaData.setContentLength(this.objectData.available());
            } catch (IOException ex) {
                Logger.warn("Can't retrieve stream available size", ex);
            } finally {
                try {
                    this.objectData.reset();
                } catch (IOException ex) {
                    Logger.error("Can't reset stream position", ex);
                }
            }
            /*
            try {
                final byte[] resultByte = DigestUtils.md5(this.objectData);
                objMetaData.setContentMD5(new String(Base64.encodeBase64(resultByte)));
            } catch (IOException ex) {
                Logger.warn("Can't compute stream MD5", ex);
            } finally {
                try {
                    this.objectData.reset();
                } catch (IOException ex) {
                    Logger.error("Can't reset stream position", ex);
                }
            }*/

            // Upload file to S3
            final PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, this.getActualFileName(), this.objectData, objMetaData);
            putObjectRequest.withCannedAcl(this.isPrivate ? CannedAccessControlList.Private : CannedAccessControlList.PublicRead);

            this.s3module.getService().putObject(putObjectRequest);
            try {
                if (this.objectData != null) {
                    this.objectData.close();
                }
            } catch (IOException ignore) {
            }

            // Save object on database
            super.save();
        }
    }

    /**
     * Delete the remote file.
     *
     * @since 16.03.13
     */
    @PreRemove
    public void deleteRemoteFile() {
        if (this.s3module == null) {
            this.s3module = Play.application().injector().instanceOf(AmazonS3Module.class);
        }
        if (this.s3module == null) {
            Logger.error("Could not delete S3 file because amazonS3 variable is null");
            throw new RuntimeException("Could not delete");
        } else {
            try {
                this.s3module.getService().deleteObject(bucket, getActualFileName());
            } catch (AmazonS3Exception ex) {
                Logger.warn("Something goes wrong with Amazon S3", ex);
            }
        }
    }

    /**
     * Get the file content. In case of error (network error, file not
     * found, ...), this method will return null.
     *
     * @return The file content, otherwise, null
     * @see InputStream
     * @since 16.03.13
     */
    public InputStream getFileContent() {
        if (this.s3module == null) {
            this.s3module = Play.application().injector().instanceOf(AmazonS3Module.class);
        }
        final S3Object obj = this.s3module.getService().getObject(bucket, getActualFileName());
        if (obj != null) {
            return obj.getObjectContent();
        }
        return null;
    }
}
