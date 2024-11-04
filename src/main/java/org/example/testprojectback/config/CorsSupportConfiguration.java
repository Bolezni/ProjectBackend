package org.example.testprojectback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(CorsSupportProperties.class)
public class CorsSupportConfiguration {

    @Bean
    @ConditionalOnExpression("${in.clouthink.daas.sbb.support.cors.enabled:true}")
    @Autowired
    public FilterRegistrationBean filterRegistrationBean(CorsSupportProperties corsSupportProperties) {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();

        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(corsSupportProperties.isAllowCredentials());
        corsConfiguration.addAllowedOrigin(corsSupportProperties.getAllowOrigin());
        corsConfiguration.addAllowedHeader(corsSupportProperties.getAllowHeader());
        corsConfiguration.addAllowedMethod(corsSupportProperties.getAllowMethod());

        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        CorsFilter corsFilter = new CorsFilter(urlBasedCorsConfigurationSource);
        FilterRegistrationBean registration = new FilterRegistrationBean(corsFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(corsSupportProperties.getOrder());
        return registration;
    }

}
