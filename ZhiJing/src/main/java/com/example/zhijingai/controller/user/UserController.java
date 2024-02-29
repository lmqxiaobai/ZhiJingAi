package com.example.zhijingai.controller.user;

import com.example.zhijingai.demo.constant.IpAddressConstant;
import com.example.zhijingai.demo.constant.MessageConstant;
import com.example.zhijingai.demo.properties.JwtProperties;
import com.example.zhijingai.demo.utils.*;
import com.example.zhijingai.entitys.dto.UserLoginDTO;
import com.example.zhijingai.entitys.dto.UserRegisterDTO;
import com.example.zhijingai.entitys.entity.User;
import com.example.zhijingai.entitys.vo.UserLoginVO;
import com.example.zhijingai.result.Result;
import com.example.zhijingai.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户管理")
@CrossOrigin(origins = IpAddressConstant.IPADDRESS_OPEN)
public class UserController {

    @Autowired
    private UserService userService;
    // 自动装配jwt参数封装类对象，以便获取相关参数
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation("用户登录接口")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("员工登录:{}",userLoginDTO);
        User user = userService.login(userLoginDTO);

        // 登录成功，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        // 保存登录用户id,然后每次验证通过的时候会解析jwt，然后获取id，将其存入线程，供后面调用
        claims.put("userId",user.getId());
        // 给jwt工具类传递参数（jwt密钥，jwt过期时间，设置的信息（用户id））
        String token = JwtUtil.createJWT(
                // 签名算法
                jwtProperties.getAdminSecretKey(),
                // 有效期
                jwtProperties.getAdminTtl(),
                // 自定义内容（载荷）
                claims
        );

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .mailbox(user.getMailbox())
                .password(user.getPassword())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }



    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation("用户注册接口")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO){
        // 验证码比对
        String mailbox = userRegisterDTO.getMailbox();
        // 根据邮箱从工具类里面获取保存的验证码
        String code = CodeUtil.codeMessage.get(mailbox);
        if(code == null){
            return Result.error(MessageConstant.SMS_CODE_FILE);
        }else if (code.equals(userRegisterDTO.getSmsCode())){
            // 验证码正确，用户注册
            if (userService.register(userRegisterDTO)){
                return Result.success();
            }else {
                return Result.error(MessageConstant.ACCOUNT_EXIST);
            }
        }else {
            return Result.error(MessageConstant.SMS_CODE_ERROR);
        }
    }

    /**
     * 邮箱验证码获取
     */
    @ApiOperation("邮箱验证码获取接口")
    @PostMapping("/mailbox")
    public Result getEmilCode(@RequestParam String mailbox){

        //判断该邮箱是否已注册(后期将邮箱账号也添加到用户里面，然后传递过来的邮箱账号去查询是否存在来判定邮箱是否已经注册)
        if(userService.queryMailbox(mailbox)){
            //产生随机6位验证码
            String code = RandomUtil.randomCode();

            String emileContext = "【智境AI】您正在注册“智境AI”账户，您的验证码是" + code +"，请勿泄露您的验证码";

            try {
                //调用邮件发送函数工具类
                EmileUtil.sendEmail(mailbox,"验证码",emileContext);

                //启动验证码时效计时，若超过5分钟未使用则验证码失效（测试期间为1分钟）
                CodeUtil codeUtil = new CodeUtil();
                codeUtil.timerCache(mailbox,code);

                //返回发送成功
                return Result.success("已发送");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            return Result.error("邮箱已注册");
        }
    }

}
