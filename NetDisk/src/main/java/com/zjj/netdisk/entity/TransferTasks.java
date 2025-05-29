package com.zjj.netdisk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 传输任务表，支持断点续传和任务控制
 * @TableName transfer_tasks
 */
@TableName(value ="transfer_tasks")
@Data
public class TransferTasks {
    /**
     * 传输任务的唯一标识符 (例如 UUID)，主键
     */
    @TableId
    private String taskId;

    /**
     * 发起传输的用户 user_id
     */
    private Long userId;

    /**
     * 对于下载任务，关联到要下载的 user_files 条目
     */
    private Long userFileId;

    /**
     * 对于上传任务，指定上传到哪个父文件夹下
     */
    private Long targetParentFolderId;

    /**
     * 任务类型
     */
    private Object taskType;

    /**
     * 传输的文件名
     */
    private String fileName;

    /**
     * 文件的总大小
     */
    private Long totalSizeBytes;

    /**
     * 已传输的字节数
     */
    private Long transferredBytes;

    /**
     * 任务当前状态
     */
    private Object status;

    /**
     * 对于分块传输，存储已完成的分块信息
     */
    private Object chunkInfo;

    /**
     * 上传完成后，文件的最终哈希
     */
    private String fileHashFk;

    /**
     * 任务创建时间
     */
    private Date creationTs;

    /**
     * 任务最后活动时间
     */
    private Date lastActivityTs;

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
        TransferTasks other = (TransferTasks) that;
        return (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getUserFileId() == null ? other.getUserFileId() == null : this.getUserFileId().equals(other.getUserFileId()))
            && (this.getTargetParentFolderId() == null ? other.getTargetParentFolderId() == null : this.getTargetParentFolderId().equals(other.getTargetParentFolderId()))
            && (this.getTaskType() == null ? other.getTaskType() == null : this.getTaskType().equals(other.getTaskType()))
            && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
            && (this.getTotalSizeBytes() == null ? other.getTotalSizeBytes() == null : this.getTotalSizeBytes().equals(other.getTotalSizeBytes()))
            && (this.getTransferredBytes() == null ? other.getTransferredBytes() == null : this.getTransferredBytes().equals(other.getTransferredBytes()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getChunkInfo() == null ? other.getChunkInfo() == null : this.getChunkInfo().equals(other.getChunkInfo()))
            && (this.getFileHashFk() == null ? other.getFileHashFk() == null : this.getFileHashFk().equals(other.getFileHashFk()))
            && (this.getCreationTs() == null ? other.getCreationTs() == null : this.getCreationTs().equals(other.getCreationTs()))
            && (this.getLastActivityTs() == null ? other.getLastActivityTs() == null : this.getLastActivityTs().equals(other.getLastActivityTs()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUserFileId() == null) ? 0 : getUserFileId().hashCode());
        result = prime * result + ((getTargetParentFolderId() == null) ? 0 : getTargetParentFolderId().hashCode());
        result = prime * result + ((getTaskType() == null) ? 0 : getTaskType().hashCode());
        result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
        result = prime * result + ((getTotalSizeBytes() == null) ? 0 : getTotalSizeBytes().hashCode());
        result = prime * result + ((getTransferredBytes() == null) ? 0 : getTransferredBytes().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getChunkInfo() == null) ? 0 : getChunkInfo().hashCode());
        result = prime * result + ((getFileHashFk() == null) ? 0 : getFileHashFk().hashCode());
        result = prime * result + ((getCreationTs() == null) ? 0 : getCreationTs().hashCode());
        result = prime * result + ((getLastActivityTs() == null) ? 0 : getLastActivityTs().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", taskId=").append(taskId);
        sb.append(", userId=").append(userId);
        sb.append(", userFileId=").append(userFileId);
        sb.append(", targetParentFolderId=").append(targetParentFolderId);
        sb.append(", taskType=").append(taskType);
        sb.append(", fileName=").append(fileName);
        sb.append(", totalSizeBytes=").append(totalSizeBytes);
        sb.append(", transferredBytes=").append(transferredBytes);
        sb.append(", status=").append(status);
        sb.append(", chunkInfo=").append(chunkInfo);
        sb.append(", fileHashFk=").append(fileHashFk);
        sb.append(", creationTs=").append(creationTs);
        sb.append(", lastActivityTs=").append(lastActivityTs);
        sb.append("]");
        return sb.toString();
    }
}