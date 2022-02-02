package fhw.gulimall.product.exception;

import fhw.gulimall.common.exception.BizCodeEnum;
import fhw.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-19 21:26
 */
@Slf4j
@RestControllerAdvice(basePackages = "fhw.gulimall.product.app")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handleValidException(MethodArgumentNotValidException exception) {
        log.error("数据校验出现问题{},异常类型{}", exception.getMessage(), exception.getClass());
        BindingResult bindingResult = exception.getBindingResult();
        Map<String, String> result = new HashMap<>();
        bindingResult.getFieldErrors().stream().forEach((item) -> {
            String defaultMessage = item.getDefaultMessage();
            String field = item.getField();
            result.put(field, defaultMessage);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION).put("data", result);
    }

//    @ExceptionHandler(value = {Throwable.class})
//    public R handleException(Throwable throwable) {
//        log.error("系统未知异常{},异常类型{}", throwable.getMessage(), throwable.getClass());
//        return R.error(BizCodeEnum.UNKONW_EXCEPTION);
//    }

}
