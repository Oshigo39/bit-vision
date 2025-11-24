package com.chiho.bitvision.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Jwt令牌工具类
 */
public class JwtUtils {
    // 常量
    public static final long EXPIRE = 10000000L * 60 * 60 * 24;     // 过期时间
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    // 生成token的方法（根据ID和昵称）
    public static String getJwtToken(Long id, String nickname ){

        return Jwts.builder()
                // 1. 设置Header
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")

                // 2. 设置Payload的标准声明
                .setSubject("guli-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                // 3. 设置Payload的自定义声明
                .claim("id", String.valueOf(id))  // 存储用户ID（转字符串避免序列化问题）
                .claim("nickname", nickname)

                // 4. 指定Signature的算法与签名
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();     // 拼接
    }

    // 根据传入的Jwt判断token是否存在或者有效
    public static boolean checkToken(String jwtToken){
        if (StringUtils.isEmpty(jwtToken)) return false;
        try{
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        }catch (Exception e){
            log.info("Jwt校验失败：{}",e.getMessage());
            return false;
        }
        return true;
    }

    // 根据传入的request判断token是否存在与有效
    public static boolean checkToken(HttpServletRequest request){
        try{
            String jwtToken = request.getHeader("token");
            if (ObjectUtils.isEmpty(jwtToken)) return false;
        } catch (Exception e){
            log.info("request的token校验失败：{}",e.getMessage());
            return false;
        }
        return true;
    }

    public static Long getUserId(HttpServletRequest request){
        // 1. 从HTTP请求头部当中获取JWT令牌
        String jwtToken = request.getHeader("token");
        // 2. 令牌为空时返回null，避免空指针解析异常
        if (ObjectUtils.isEmpty(jwtToken)) return null;
        // 3. 解析JWT令牌并验证签名，得到包含载荷的Jws对象
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(APP_SECRET)
                .parseClaimsJws(jwtToken);      // 解析声明Jws
        // 4. 获取JWT的载荷（Payload）部分，载荷中存储了用户ID等自定义数据
        Claims claims = claimsJws.getBody();
        // 5. 从载荷中提取"id"字段，转换为Long类型并返回
        return Long.valueOf(claims.get("id").toString());
    }

}
