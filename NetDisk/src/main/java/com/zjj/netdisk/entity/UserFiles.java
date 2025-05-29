package com.zjj.netdisk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户文件/文件夹表，代表用户视角的文件和文件夹
 * @TableName user_files
 */
@TableName(value ="user_files")
@Data
public class UserFiles {
    /**
     * 文件或文件夹的唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long fileId;

    /**
     * 文件/文件夹的拥有者
     */
    private Long userId;

    /**
     * 父文件夹的 file_id，NULL表示根目录
     */
    private Long parentFolderId;

    /**
     * 文件或文件夹的名称
     */
    private String itemName;

    /**
     * 条目类型 (file:文件, folder:文件夹)
     */
    private Object itemType;

    /**
     * 文件的 MIME 类型
     */
    private String mimeType;

    /**
     * 文件的逻辑大小（字节），文件夹可为NULL
     */
    private Long fileSizeBytes;

    /**
     * 对于文件类型，关联到 physical_files.file_hash
     */
    private String fileHashFk;

    /**
     * 文件/文件夹的状态 (active:正常, in_recycle_bin:在回收站, permanently_deleted:永久删除)
     */
    private Object status;

    /**
     * 条目创建时间戳
     */
    private Date creationTs;

    /**
     * 条目最后修改时间戳
     */
    private Date modificationTs;

    /**
     * 条目被移入回收站的时间戳
     */
    private Date deletedTs;

    /**
     * 在回收站时，记录其原始父文件夹ID
     */
    private Long originalParentId;

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
        UserFiles other = (UserFiles) that;
        return (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getParentFolderId() == null ? other.getParentFolderId() == null : this.getParentFolderId().equals(other.getParentFolderId()))
            && (this.getItemName() == null ? other.getItemName() == null : this.getItemName().equals(other.getItemName()))
            && (this.getItemType() == null ? other.getItemType() == null : this.getItemType().equals(other.getItemType()))
            && (this.getMimeType() == null ? other.getMimeType() == null : this.getMimeType().equals(other.getMimeType()))
            && (this.getFileSizeBytes() == null ? other.getFileSizeBytes() == null : this.getFileSizeBytes().equals(other.getFileSizeBytes()))
            && (this.getFileHashFk() == null ? other.getFileHashFk() == null : this.getFileHashFk().equals(other.getFileHashFk()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreationTs() == null ? other.getCreationTs() == null : this.getCreationTs().equals(other.getCreationTs()))
            && (this.getModificationTs() == null ? other.getModificationTs() == null : this.getModificationTs().equals(other.getModificationTs()))
            && (this.getDeletedTs() == null ? other.getDeletedTs() == null : this.getDeletedTs().equals(other.getDeletedTs()))
            && (this.getOriginalParentId() == null ? other.getOriginalParentId() == null : this.getOriginalParentId().equals(other.getOriginalParentId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getParentFolderId() == null) ? 0 : getParentFolderId().hashCode());
        result = prime * result + ((getItemName() == null) ? 0 : getItemName().hashCode());
        result = prime * result + ((getItemType() == null) ? 0 : getItemType().hashCode());
        result = prime * result + ((getMimeType() == null) ? 0 : getMimeType().hashCode());
        result = prime * result + ((getFileSizeBytes() == null) ? 0 : getFileSizeBytes().hashCode());
        result = prime * result + ((getFileHashFk() == null) ? 0 : getFileHashFk().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreationTs() == null) ? 0 : getCreationTs().hashCode());
        result = prime * result + ((getModificationTs() == null) ? 0 : getModificationTs().hashCode());
        result = prime * result + ((getDeletedTs() == null) ? 0 : getDeletedTs().hashCode());
        result = prime * result + ((getOriginalParentId() == null) ? 0 : getOriginalParentId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fileId=").append(fileId);
        sb.append(", userId=").append(userId);
        sb.append(", parentFolderId=").append(parentFolderId);
        sb.append(", itemName=").append(itemName);
        sb.append(", itemType=").append(itemType);
        sb.append(", mimeType=").append(mimeType);
        sb.append(", fileSizeBytes=").append(fileSizeBytes);
        sb.append(", fileHashFk=").append(fileHashFk);
        sb.append(", status=").append(status);
        sb.append(", creationTs=").append(creationTs);
        sb.append(", modificationTs=").append(modificationTs);
        sb.append(", deletedTs=").append(deletedTs);
        sb.append(", originalParentId=").append(originalParentId);
        sb.append("]");
        return sb.toString();
    }
}