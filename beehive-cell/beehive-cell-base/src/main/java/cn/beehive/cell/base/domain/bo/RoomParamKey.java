package cn.beehive.cell.base.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Room Param 的Key
 * 因为Room的参数是根据 cellId + roomId + userId 一同确定的
 *
 * @author CoDeleven
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomParamKey {
    private Integer configId;
    private Integer roomId;
    private Integer userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoomParamKey that = (RoomParamKey) o;

        return new EqualsBuilder().append(configId, that.configId).append(roomId, that.roomId).append(userId, that.userId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(configId).append(roomId).append(userId).toHashCode();
    }
}
