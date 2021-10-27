package edu.hunnu.msmservice.controller;

import edu.hunnu.commonutils.R;
import edu.hunnu.msmservice.service.MsmService;
import edu.hunnu.msmservice.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/edumsm/msm")
@CrossOrigin
public class MsmController {

    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //发送短信的方法
    @GetMapping("send/{phone}")
    public R sendMsm(@PathVariable String phone){
        // 1、从redis获取验证码,如果获取到直接返回
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)){
            return R.ok();
        }
        // 2、如果获取不到，进行阿里云发送
        // 生成随机值，传递阿里云发送
        code = RandomUtil.getFourBitRandom();
        System.out.println("手机"+phone+"的最新验证码为"+code);
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        //调用service发送短信的方法
//        boolean isSend = msmService.send(param,phone);
        //模拟发送成功 因为阿里云短信服务无法通过审核
        boolean isSend = true;
        if (isSend){
            // 发送成功，把发送成功验证码放到redis里面
            // 设置有效时间  表示5分钟有效时间
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error().message("短信发送失败");
        }
    }
}
