package com.lhf.resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * <p>资源服务器</p>
 *
 * @author zy 刘会发
 * @version 1.0
 * @since 2020/5/11
 */
@Configuration
@EnableResourceServer//开启资源服务自动配置
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 远程token验证，就是验证token时到授权服务验证
     * @return
     */
    @Bean
    public RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices bean = new RemoteTokenServices();
        bean.setClientId("novel");
        bean.setClientSecret("123");
        bean.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");//验证地址
        return bean;
    }

    /**
     * 资源配置
     * @param resources
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(remoteTokenServices())
                .resourceId("lhf");
    }

    /**
     * 安全配置
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").hasRole("admin")
                .anyRequest().authenticated();
    }
}
