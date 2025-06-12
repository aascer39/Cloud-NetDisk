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
 * 消息通知表
 * @TableName notifications
 */
@TableName(value ="notifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsDTO {
    /**
     * 通知唯一标识符，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long notificationId;

    /**
     * 接收通知的用户 user_id
     */
    private Long userId;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String message;

    /**
     * 指向相关资源的可选链接
     */
    private String link;

    /**
     * 标记通知是否已读
     */
    private Integer isRead;

    /**
     * 通知类型 (例如 NEW_SHARE, QUOTA_WARNING)
     */
    private String notificationType;

    /**
     * 通知创建时间戳
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
        NotificationsDTO other = (NotificationsDTO) that;
        return (this.getNotificationId() == null ? other.getNotificationId() == null : this.getNotificationId().equals(other.getNotificationId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getMessage() == null ? other.getMessage() == null : this.getMessage().equals(other.getMessage()))
            && (this.getLink() == null ? other.getLink() == null : this.getLink().equals(other.getLink()))
            && (this.getIsRead() == null ? other.getIsRead() == null : this.getIsRead().equals(other.getIsRead()))
            && (this.getNotificationType() == null ? other.getNotificationType() == null : this.getNotificationType().equals(other.getNotificationType()))
            && (this.getCreationTs() == null ? other.getCreationTs() == null : this.getCreationTs().equals(other.getCreationTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getNotificationId() == null) ? 0 : getNotificationId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getMessage() == null) ? 0 : getMessage().hashCode());
        result = prime * result + ((getLink() == null) ? 0 : getLink().hashCode());
        result = prime * result + ((getIsRead() == null) ? 0 : getIsRead().hashCode());
        result = prime * result + ((getNotificationType() == null) ? 0 : getNotificationType().hashCode());
        result = prime * result + ((getCreationTs() == null) ? 0 : getCreationTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", notificationId=").append(notificationId);
        sb.append(", userId=").append(userId);
        sb.append(", title=").append(title);
        sb.append(", message=").append(message);
        sb.append(", link=").append(link);
        sb.append(", isRead=").append(isRead);
        sb.append(", notificationType=").append(notificationType);
        sb.append(", creationTs=").append(creationTs);
        sb.append("]");
        return sb.toString();
    }
}