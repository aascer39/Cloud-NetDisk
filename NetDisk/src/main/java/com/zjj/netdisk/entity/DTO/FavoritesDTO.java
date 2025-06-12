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
 * 用户收藏的文件或文件夹的关联表
 * @author 34978
 * @TableName favorites
 */
@TableName(value ="favorites")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesDTO {
    /**
     * 收藏记录的唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long favoriteId;

    /**
     * 进行收藏操作的用户 user_id
     */
    private Long userId;

    /**
     * 被收藏的文件或文件夹的 file_id
     */
    private Long userFileId;

    /**
     * 收藏创建时间戳
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
        FavoritesDTO other = (FavoritesDTO) that;
        return (this.getFavoriteId() == null ? other.getFavoriteId() == null : this.getFavoriteId().equals(other.getFavoriteId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserFileId() == null ? other.getUserFileId() == null : this.getUserFileId().equals(other.getUserFileId()))
            && (this.getCreationTs() == null ? other.getCreationTs() == null : this.getCreationTs().equals(other.getCreationTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFavoriteId() == null) ? 0 : getFavoriteId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserFileId() == null) ? 0 : getUserFileId().hashCode());
        result = prime * result + ((getCreationTs() == null) ? 0 : getCreationTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", favoriteId=").append(favoriteId);
        sb.append(", userId=").append(userId);
        sb.append(", userFileId=").append(userFileId);
        sb.append(", creationTs=").append(creationTs);
        sb.append("]");
        return sb.toString();
    }
}