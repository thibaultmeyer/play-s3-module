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

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * This Ebean model represent a file stored on Amazon S3.
 *
 * @author Thibault Meyer
 * @version 16.03.19
 * @see BaseS3FileModel
 * @since 16.03.13
 */
@Entity
@Table(name = "s3file")
public class S3FileModel extends BaseS3FileModel implements Cloneable {

    /**
     * Helpers to request model.
     *
     * @since 16.03.13
     */
    public static final Model.Finder<UUID, S3FileModel> find = new Finder<>(S3FileModel.class);

    /**
     * Clone the current object. ID and name fields will not be cloned.
     *
     * @return The cloned object
     * @since 16.03.14
     */
    @Override
    public S3FileModel clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException ignore) {
        }
        final S3FileModel s3FileModel = new S3FileModel();
        final StringBuilder newNameBuilder = new StringBuilder();
        final int lastIdx = this.name.lastIndexOf(".");
        if (lastIdx != -1) {
            newNameBuilder.append(this.name.substring(0, lastIdx));
            newNameBuilder.append('_');
            newNameBuilder.append(System.currentTimeMillis());
            newNameBuilder.append('.');
            newNameBuilder.append(this.name.substring(lastIdx + 1));
        } else {
            newNameBuilder.append(this.name);
            newNameBuilder.append('_');
            newNameBuilder.append(System.currentTimeMillis());
        }
        s3FileModel.setName(newNameBuilder.toString());
        s3FileModel.setContentType(this.contentType);
        s3FileModel.setPrivate(this.isPrivate);
        s3FileModel.setSubDirectory(this.subDirectory);
        s3FileModel.setObject(this.getFileContent());
        s3FileModel.save();
        return s3FileModel;
    }
}
