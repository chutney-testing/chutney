/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.storage.jpa;

import jakarta.persistence.AttributeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang3.StringUtils;

public class ReportConverter implements AttributeConverter<String, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(String report) {
        if (StringUtils.isNoneEmpty()) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {

                gzipOutputStream.write(report.getBytes(StandardCharsets.UTF_8));
                gzipOutputStream.finish();
                return byteArrayOutputStream.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException("Failed to compress report content", e);
            }
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(byte[] zippedReport) {
        if (zippedReport == null || zippedReport.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zippedReport);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toString(StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Failed to decompress report content", e);
        }
    }
}
