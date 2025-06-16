package com.zjj.netdisk.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息表
 *
 * @author 34978
 * @TableName users
 */
@TableName(value = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users implements Serializable {
    /**
     * 用户唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名，唯一且不能为空
     */
    private String username;

    /**
     * 哈希后的用户密码，确保安全。第三方登录时可为空
     */
    private String passwordHash;

    /**
     * 用户邮箱，唯一，可为空
     */
    private String email;

    /**
     * 用户头像URL，可从QQ获取
     */
    private String avatarUrl;

    /**
     * 用户注册时间戳
     */
    private Date registrationTs;

    /**
     * 用户最后登录时间戳
     */
    private Date lastLoginTs;

    /**
     * 用户账户状态 (active:活跃, suspended:禁用, deleted:已删除)
     */
    private Object status;

    /**
     * 分配给用户的总存储空间大小（字节）
     */
    private Long storageQuotaBytes;

    /**
     * 用户当前已使用的存储空间大小（字节）
     */
    private Long usedStorageBytes;

    /**
     * 用户最后修改密码时间戳，可为空
     */
    private Date lastPasswordUpdateTs;

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
        Users other = (Users) that;
        return (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
                && (this.getPasswordHash() == null ? other.getPasswordHash() == null : this.getPasswordHash().equals(other.getPasswordHash()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getAvatarUrl() == null ? other.getAvatarUrl() == null : this.getAvatarUrl().equals(other.getAvatarUrl()))
                && (this.getRegistrationTs() == null ? other.getRegistrationTs() == null : this.getRegistrationTs().equals(other.getRegistrationTs()))
                && (this.getLastLoginTs() == null ? other.getLastLoginTs() == null : this.getLastLoginTs().equals(other.getLastLoginTs()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getStorageQuotaBytes() == null ? other.getStorageQuotaBytes() == null : this.getStorageQuotaBytes().equals(other.getStorageQuotaBytes()))
                && (this.getUsedStorageBytes() == null ? other.getUsedStorageBytes() == null : this.getUsedStorageBytes().equals(other.getUsedStorageBytes()))
                && (this.getLastPasswordUpdateTs() == null ? other.getLastPasswordUpdateTs() == null : this.getLastPasswordUpdateTs().equals(other.getLastPasswordUpdateTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPasswordHash() == null) ? 0 : getPasswordHash().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getAvatarUrl() == null) ? 0 : getAvatarUrl().hashCode());
        result = prime * result + ((getRegistrationTs() == null) ? 0 : getRegistrationTs().hashCode());
        result = prime * result + ((getLastLoginTs() == null) ? 0 : getLastLoginTs().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getStorageQuotaBytes() == null) ? 0 : getStorageQuotaBytes().hashCode());
        result = prime * result + ((getUsedStorageBytes() == null) ? 0 : getUsedStorageBytes().hashCode());
        result = prime * result + ((getLastPasswordUpdateTs() == null) ? 0 : getLastPasswordUpdateTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", username=").append(username);
        sb.append(", passwordHash=").append(passwordHash);
        sb.append(", email=").append(email);
        sb.append(", avatarUrl=").append(avatarUrl);
        sb.append(", registrationTs=").append(registrationTs);
        sb.append(", lastLoginTs=").append(lastLoginTs);
        sb.append(", status=").append(status);
        sb.append(", storageQuotaBytes=").append(storageQuotaBytes);
        sb.append(", usedStorageBytes=").append(usedStorageBytes);
        sb.append(", lastPasswordUpdateTs=").append(lastPasswordUpdateTs);
        sb.append("]");
        return sb.toString();
    }
}