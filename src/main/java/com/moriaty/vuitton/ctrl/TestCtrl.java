package com.moriaty.vuitton.ctrl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 测试 Ctrl
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午4:30
 */
@RestController
@RequestMapping("test")
@AllArgsConstructor
@Slf4j
public class TestCtrl {

    @GetMapping("testActuator")
    public String testActuator() {
        return "success";
    }
}
