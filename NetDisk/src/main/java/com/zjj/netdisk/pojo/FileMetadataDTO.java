package com.zjj.netdisk.pojo;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.minio.StatObjectResponse;
import lombok.Builder;
import lombok.Data;

/**
 * @author 34978
 */
@Data
@Builder
public class FileMetadataDTO {
    private String bucketName;
    private String objectName;
    private long size;
    private String etag;
    private String contentType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") // ISO 8601 格式
    private ZonedDateTime lastModified;


    // 一个静态方法用于从 StatObjectResponse 转换成json
    public static FileMetadataDTO fromStatObjectResponse(StatObjectResponse stat) {
        if (stat == null) {
            return null;
        }
        return FileMetadataDTO.builder()
                .bucketName(stat.bucket())
                .objectName(stat.object())
                .size(stat.size())
                .etag(stat.etag())
                .contentType(stat.contentType())
                .lastModified(stat.lastModified())
                .build();
    }
}