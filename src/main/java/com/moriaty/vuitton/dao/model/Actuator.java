package com.moriaty.vuitton.dao.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 执行器表
 * </p>
 *
 * @author Moriaty
 * @since 2024-04-08 17:58:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class Actuator {

    private String id;

    private String meta;

    private String stepList;

    private String stepDataUrl;

    private Boolean interrupt;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
