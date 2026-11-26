package com.ruoyi.wms.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端 IP 查询（前端用于兜底获取本机 IP，随请求头 X-Client-IP 上报）
 */
@RestController
@RequestMapping
public class ClientIpController {

    @GetMapping("/client/ip")
    public R<String> clientIp() {
        R<String> result = R.ok();
        result.setData(ServletUtils.getClientIP());
        return result;
    }
}
