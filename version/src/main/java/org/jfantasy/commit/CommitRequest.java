package org.jfantasy.commit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author ChenWenJie
 * @Data 2020/11/17 2:55 下午
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommitRequest {
    private String code;
    private String employeeId;
    private String id;
    private Boolean deleted;
    private String jsonData;
}
