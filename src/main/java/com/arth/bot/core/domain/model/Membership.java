package com.arth.bot.core.domain.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName t_membership
 */
@TableName(value ="t_membership")
@Data
public class Membership {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long groupId;

    /**
     * 
     */
    private String role;

    /**
     * 
     */
    private String noteName;

    /**
     * 
     */
    private LocalDateTime joinedAt;

    /**
     * 
     */
    private LocalDateTime lastSeenAt;

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
        Membership other = (Membership) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()))
            && (this.getNoteName() == null ? other.getNoteName() == null : this.getNoteName().equals(other.getNoteName()))
            && (this.getJoinedAt() == null ? other.getJoinedAt() == null : this.getJoinedAt().equals(other.getJoinedAt()))
            && (this.getLastSeenAt() == null ? other.getLastSeenAt() == null : this.getLastSeenAt().equals(other.getLastSeenAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        result = prime * result + ((getNoteName() == null) ? 0 : getNoteName().hashCode());
        result = prime * result + ((getJoinedAt() == null) ? 0 : getJoinedAt().hashCode());
        result = prime * result + ((getLastSeenAt() == null) ? 0 : getLastSeenAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", userId=" + userId +
                ", groupId=" + groupId +
                ", role=" + role +
                ", noteName=" + noteName +
                ", joinedAt=" + joinedAt +
                ", lastSeenAt=" + lastSeenAt +
                "]";
        return sb;
    }
}