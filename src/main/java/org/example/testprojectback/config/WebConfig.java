package org.example.testprojectback.config;

import org.example.testprojectback.filter.ActivationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<ActivationFilter> customActivationFilter() {
        FilterRegistrationBean<ActivationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ActivationFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
