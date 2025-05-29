package com.zjj.netdisk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户活动日志表
 * @TableName user_activities
 */
@TableName(value ="user_activities")
@Data
public class UserActivities {
    /**
     * 活动记录的唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long activityId;

    /**
     * 执行活动的用户 user_id
     */
    private Long userId;

    /**
     * 活动类型 (例如 UPLOAD_FILE, CREATE_FOLDER, LOGIN)
     */
    private String activityType;

    /**
     * 操作所关联的文件或文件夹 file_id (如果适用)
     */
    private Long targetFileId;

    /**
     * 活动的详细信息 (例如 IP 地址、分享的令牌)
     */
    private String details;

    /**
     * 活动发生的时间戳
     */
    private Date activityTs;

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
        UserActivities other = (UserActivities) that;
        return (this.getActivityId() == null ? other.getActivityId() == null : this.getActivityId().equals(other.getActivityId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getActivityType() == null ? other.getActivityType() == null : this.getActivityType().equals(other.getActivityType()))
            && (this.getTargetFileId() == null ? other.getTargetFileId() == null : this.getTargetFileId().equals(other.getTargetFileId()))
            && (this.getDetails() == null ? other.getDetails() == null : this.getDetails().equals(other.getDetails()))
            && (this.getActivityTs() == null ? other.getActivityTs() == null : this.getActivityTs().equals(other.getActivityTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getActivityId() == null) ? 0 : getActivityId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getActivityType() == null) ? 0 : getActivityType().hashCode());
        result = prime * result + ((getTargetFileId() == null) ? 0 : getTargetFileId().hashCode());
        result = prime * result + ((getDetails() == null) ? 0 : getDetails().hashCode());
        result = prime * result + ((getActivityTs() == null) ? 0 : getActivityTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", activityId=").append(activityId);
        sb.append(", userId=").append(userId);
        sb.append(", activityType=").append(activityType);
        sb.append(", targetFileId=").append(targetFileId);
        sb.append(", details=").append(details);
        sb.append(", activityTs=").append(activityTs);
        sb.append("]");
        return sb.toString();
    }
}