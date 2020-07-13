package com.imooc.coupon.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * <h1>定制HTTP消息转换器</h1>
 *
 * @author ZhouFufeng
 * @since 2020/7/10
 **/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //清空原有
        converters.clear();
        converters.add(new MappingJackson2HttpMessageConverter());

    }
}
