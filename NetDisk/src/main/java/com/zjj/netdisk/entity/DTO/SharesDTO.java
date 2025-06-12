package com.zjj.netdisk.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 分享记录表
 * @TableName shares
 */
@TableName(value ="shares")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharesDTO {
    /**
     * 分享记录的唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long shareId;

    /**
     * 被分享的文件或文件夹的 file_id
     */
    private Long userFileId;

    /**
     * 创建分享的用户 user_id
     */
    private Long sharerUserId;

    /**
     * 用于公开访问的唯一分享令牌（链接）
     */
    private String shareToken;

    /**
     * 分享链接的访问密码（哈希存储），可为空
     */
    private String passwordHash;

    /**
     * 分享链接的过期时间戳，可为空（表示永不过期）
     */
    private Date expiryTs;

    /**
     * 分享创建时间戳
     */
    private Date creationTs;

    /**
     * 分享链接的访问次数统计
     */
    private Integer accessCount;

    /**
     * 定义分享的权限 (例如 {"can_download": true, "can_preview": true})
     */
    private Object permissions;

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
        SharesDTO other = (SharesDTO) that;
        return (this.getShareId() == null ? other.getShareId() == null : this.getShareId().equals(other.getShareId()))
            && (this.getUserFileId() == null ? other.getUserFileId() == null : this.getUserFileId().equals(other.getUserFileId()))
            && (this.getSharerUserId() == null ? other.getSharerUserId() == null : this.getSharerUserId().equals(other.getSharerUserId()))
            && (this.getShareToken() == null ? other.getShareToken() == null : this.getShareToken().equals(other.getShareToken()))
            && (this.getPasswordHash() == null ? other.getPasswordHash() == null : this.getPasswordHash().equals(other.getPasswordHash()))
            && (this.getExpiryTs() == null ? other.getExpiryTs() == null : this.getExpiryTs().equals(other.getExpiryTs()))
            && (this.getCreationTs() == null ? other.getCreationTs() == null : this.getCreationTs().equals(other.getCreationTs()))
            && (this.getAccessCount() == null ? other.getAccessCount() == null : this.getAccessCount().equals(other.getAccessCount()))
            && (this.getPermissions() == null ? other.getPermissions() == null : this.getPermissions().equals(other.getPermissions()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShareId() == null) ? 0 : getShareId().hashCode());
        result = prime * result + ((getUserFileId() == null) ? 0 : getUserFileId().hashCode());
        result = prime * result + ((getSharerUserId() == null) ? 0 : getSharerUserId().hashCode());
        result = prime * result + ((getShareToken() == null) ? 0 : getShareToken().hashCode());
        result = prime * result + ((getPasswordHash() == null) ? 0 : getPasswordHash().hashCode());
        result = prime * result + ((getExpiryTs() == null) ? 0 : getExpiryTs().hashCode());
        result = prime * result + ((getCreationTs() == null) ? 0 : getCreationTs().hashCode());
        result = prime * result + ((getAccessCount() == null) ? 0 : getAccessCount().hashCode());
        result = prime * result + ((getPermissions() == null) ? 0 : getPermissions().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", shareId=").append(shareId);
        sb.append(", userFileId=").append(userFileId);
        sb.append(", sharerUserId=").append(sharerUserId);
        sb.append(", shareToken=").append(shareToken);
        sb.append(", passwordHash=").append(passwordHash);
        sb.append(", expiryTs=").append(expiryTs);
        sb.append(", creationTs=").append(creationTs);
        sb.append(", accessCount=").append(accessCount);
        sb.append(", permissions=").append(permissions);
        sb.append("]");
        return sb.toString();
    }
}