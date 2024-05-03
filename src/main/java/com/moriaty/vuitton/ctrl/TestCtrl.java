package com.moriaty.vuitton.ctrl;

import com.moriaty.vuitton.library.wrap.WrapMapper;
import com.moriaty.vuitton.library.wrap.Wrapper;
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

    @GetMapping("hello")
    public Wrapper<String> hello() {
        return WrapMapper.okStringData("hello world");
    }

}
