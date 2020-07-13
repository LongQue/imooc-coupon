package com.imooc.coupon.advice;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ZhouFufeng
 * @since 2020/7/13
 **/
@RestControllerAdvice
public class GlobalExceptionAdvice {
    /**
     * <h2>对 CouponException 进行统一处理</h2>
     * @param req
     * @param exception
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req, CouponException exception) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(exception.getMessage());
        return response;
    }
}
