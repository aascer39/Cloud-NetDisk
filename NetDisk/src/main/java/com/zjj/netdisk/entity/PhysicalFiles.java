package com.zjj.netdisk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 物理文件表，用于文件去重
 * @TableName physical_files
 */
@TableName(value ="physical_files")
@Data
@Builder
public class PhysicalFiles {
    /**
     * 文件内容的唯一哈希值（例如 SHA-256），作为主键
     */
    @TableId
    private String fileHash;

    /**
     * 物理文件的实际大小（字节）
     */
    private Long fileSizeBytes;

    /**
     * 文件在服务器上的实际存储路径，唯一
     */
    private String storagePath;

    /**
     * 引用计数
     */
    private Integer referenceCount;

    /**
     * 此物理文件首次被添加到系统的时间
     */
    private Date creationTs;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PhysicalFiles other = (PhysicalFiles) that;
        return (this.getFileHash() == null ? other.getFileHash() == null : this.getFileHash().equals(other.getFileHash()))
            && (this.getFileSizeBytes() == null ? other.getFileSizeBytes() == null : this.getFileSizeBytes().equals(other.getFileSizeBytes()))
            && (this.getStoragePath() == null ? other.getStoragePath() == null : this.getStoragePath().equals(other.getStoragePath()))
            && (this.getReferenceCount() == null ? other.getReferenceCount() == null : this.getReferenceCount().equals(other.getReferenceCount()))
            && (this.getCreationTs() == null ? other.getCreationTs() == null : this.getCreationTs().equals(other.getCreationTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFileHash() == null) ? 0 : getFileHash().hashCode());
        result = prime * result + ((getFileSizeBytes() == null) ? 0 : getFileSizeBytes().hashCode());
        result = prime * result + ((getStoragePath() == null) ? 0 : getStoragePath().hashCode());
        result = prime * result + ((getReferenceCount() == null) ? 0 : getReferenceCount().hashCode());
        result = prime * result + ((getCreationTs() == null) ? 0 : getCreationTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fileHash=").append(fileHash);
        sb.append(", fileSizeBytes=").append(fileSizeBytes);
        sb.append(", storagePath=").append(storagePath);
        sb.append(", referenceCount=").append(referenceCount);
        sb.append(", creationTs=").append(creationTs);
        sb.append("]");
        return sb.toString();
    }
}