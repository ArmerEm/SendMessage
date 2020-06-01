package com.atguigu.sm;

import com.atguigu.sm.utils.VerifyCodeConfig;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Random;

@RestController
public class demoController {
    @PostMapping("/sendCode")
    public String doSendCode(@RequestParam("phone_no") String phoneNo){
        //验证手机号不为空
        if(StringUtils.isEmpty(phoneNo)){
            return "-1";
        }
        // 验证发送次数3次，24小时
        // 获取计数器
        Jedis jedis = new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT);
        //设置key
        String countKey = VerifyCodeConfig.PHONE_PREFIX + phoneNo + VerifyCodeConfig.COUNT_SUFFIX;
        String countStr = jedis.get(countKey);

        // 验空
        if (countStr == null) {
            jedis.setex(countKey, VerifyCodeConfig.SECONDS_PER_DAY, "1");
        } else {
            // 判断是否3次
            int count = Integer.parseInt(countStr);
            if (count >= VerifyCodeConfig.COUNT_TIMES_1DAY) {
                jedis.close();
                return "2";
            } else {
                jedis.incr(countKey);
            }
        }
        //-----------------------------------------------------------
        // 2生成校验码,6位
        String code = getCode(VerifyCodeConfig.CODE_LEN);

        // 3保存校验码，120秒
        String codeKey = VerifyCodeConfig.PHONE_PREFIX + phoneNo + VerifyCodeConfig.PHONE_SUFFIX;
        jedis.setex(codeKey, VerifyCodeConfig.CODE_TIMEOUT, code);
        jedis.close();
        // 4发送
        System.out.println(code);
        // 5返回
       return "1";

    }

    private String getCode(int len) {
        String code = "";
        for (int i = 0; i < len; i++) {
            int rand = new Random().nextInt(10);
            code += rand;
        }
        return code;
    }
    @PostMapping("/handleCode")
    public String handleCode(@RequestParam("phone_no") String phoneNo,@RequestParam("verify_code") String code){
        //验证手机号和验证码是否为空
        if(StringUtils.isEmpty(phoneNo)||StringUtils.isEmpty(code)){
            return "2";
        }
        //获取redis的数据
        Jedis jedis =new Jedis(VerifyCodeConfig.HOST, VerifyCodeConfig.PORT);
        //获取code对应的key
        String codeKey = VerifyCodeConfig.PHONE_PREFIX + phoneNo + VerifyCodeConfig.PHONE_SUFFIX;
        System.out.println(codeKey);
        //根据key获取code的值
        String codeVal = jedis.get(codeKey);
        //再次判断codeKey和codeVal是否为空
        if(StringUtils.isEmpty(codeKey)||StringUtils.isEmpty(codeVal)){
            return "2";
        }
        //判断两者是否相同
       if(codeVal.equals(code)){
           return "1";
       }
       jedis.close();
       return "2";
    }

}
