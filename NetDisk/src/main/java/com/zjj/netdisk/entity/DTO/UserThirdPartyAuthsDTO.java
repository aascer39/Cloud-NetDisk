package com.zjj.netdisk.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户第三方认证关联表
 * @TableName user_third_party_auths
 */
@TableName(value ="user_third_party_auths")
@Data
public class UserThirdPartyAuthsDTO {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联到 users 表的主用户ID
     */
    private Long userId;

    /**
     * 第三方平台标识 (例如: QQ, WECHAT, GITHUB)
     */
    private String provider;

    /**
     * 用户在第三方平台的唯一标识符 (例如: openid, unionid)
     */
    private String providerUserId;

    /**
     * 存储从第三方平台获取的额外原始数据，如昵称、原始头像URL等
     */
    private Object extraData;

    /**
     * 绑定时间
     */
    private Date createdTs;

    /**
     * 更新时间
     */
    private Date updatedTs;

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
        UserThirdPartyAuthsDTO other = (UserThirdPartyAuthsDTO) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getProvider() == null ? other.getProvider() == null : this.getProvider().equals(other.getProvider()))
            && (this.getProviderUserId() == null ? other.getProviderUserId() == null : this.getProviderUserId().equals(other.getProviderUserId()))
            && (this.getExtraData() == null ? other.getExtraData() == null : this.getExtraData().equals(other.getExtraData()))
            && (this.getCreatedTs() == null ? other.getCreatedTs() == null : this.getCreatedTs().equals(other.getCreatedTs()))
            && (this.getUpdatedTs() == null ? other.getUpdatedTs() == null : this.getUpdatedTs().equals(other.getUpdatedTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getProvider() == null) ? 0 : getProvider().hashCode());
        result = prime * result + ((getProviderUserId() == null) ? 0 : getProviderUserId().hashCode());
        result = prime * result + ((getExtraData() == null) ? 0 : getExtraData().hashCode());
        result = prime * result + ((getCreatedTs() == null) ? 0 : getCreatedTs().hashCode());
        result = prime * result + ((getUpdatedTs() == null) ? 0 : getUpdatedTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", provider=").append(provider);
        sb.append(", providerUserId=").append(providerUserId);
        sb.append(", extraData=").append(extraData);
        sb.append(", createdTs=").append(createdTs);
        sb.append(", updatedTs=").append(updatedTs);
        sb.append("]");
        return sb.toString();
    }
}