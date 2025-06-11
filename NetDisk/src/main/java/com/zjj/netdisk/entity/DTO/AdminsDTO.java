package com.zjj.netdisk.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 管理员信息表
 * @TableName admins
 */
@TableName(value ="admins")
@Data
public class AdminsDTO {
    /**
     * 管理员唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long adminId;

    /**
     * 管理员登录名，唯一且不能为空
     */
    private String username;

    /**
     * 哈希后的管理员密码，确保安全
     */
    private String passwordHash;

    /**
     * 管理员邮箱，唯一，可为空
     */
    private String email;

    /**
     * 管理员角色 (例如: super_admin, editor)
     */
    private String role;

    /**
     * 账户创建时间戳
     */
    private Date registrationTs;

    /**
     * 最后登录时间戳
     */
    private Date lastLoginTs;

    /**
     * 管理员账户状态
     */
    private Object status;

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
        AdminsDTO other = (AdminsDTO) that;
        return (this.getAdminId() == null ? other.getAdminId() == null : this.getAdminId().equals(other.getAdminId()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPasswordHash() == null ? other.getPasswordHash() == null : this.getPasswordHash().equals(other.getPasswordHash()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()))
            && (this.getRegistrationTs() == null ? other.getRegistrationTs() == null : this.getRegistrationTs().equals(other.getRegistrationTs()))
            && (this.getLastLoginTs() == null ? other.getLastLoginTs() == null : this.getLastLoginTs().equals(other.getLastLoginTs()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPasswordHash() == null) ? 0 : getPasswordHash().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        result = prime * result + ((getRegistrationTs() == null) ? 0 : getRegistrationTs().hashCode());
        result = prime * result + ((getLastLoginTs() == null) ? 0 : getLastLoginTs().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", adminId=").append(adminId);
        sb.append(", username=").append(username);
        sb.append(", passwordHash=").append(passwordHash);
        sb.append(", email=").append(email);
        sb.append(", role=").append(role);
        sb.append(", registrationTs=").append(registrationTs);
        sb.append(", lastLoginTs=").append(lastLoginTs);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}