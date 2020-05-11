package com.lhf.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.annotation.Resource;

/**
 * <p>
 * 授权服务
 * </p>
 *
 * @author zy 刘会发
 * @version 1.0
 * @since 2020/5/11
 */
@Configuration
@EnableAuthorizationServer//开启授权服务自动配置
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * 密码比对器
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 授权安全配置
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()").allowFormAuthenticationForClients();//开启check_token给所有权限，开启表单登录模式
    }

    /**
     * 客户端配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("novel")//添加一个客户端
                .secret(passwordEncoder.encode("123"))//客户端连接密码
                .resourceIds("lhf")//资源id
//                .autoApprove(true)//自动授权
                .authorizedGrantTypes("authorization_code")//授权码模式  模式分为4种，除了授权码模式以外还有: PKCE 、 Client Credentials 、 Device Code 、 Refresh Token几种
                .scopes("all")//作用域
                .redirectUris("http://localhost:8082/index.html");//登录成功后重定向页面，可以是客户端登录页面，也可以是客户端首页
    }

    /**
     * 节点配置
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenServices(defaultTokenServices()).authorizationCodeServices(inMemoryAuthorizationCodeServices());
    }

    /**
     * spring security 提供的默认的token处理
     *
     * @return
     */
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices bean = new DefaultTokenServices();
        bean.setTokenStore(inMemoryTokenStore());
        bean.setClientDetailsService(clientDetailsService);
        bean.setAccessTokenValiditySeconds(60 * 60);
        bean.setRefreshTokenValiditySeconds(30 * 60);
        bean.setSupportRefreshToken(true);
        return bean;
    }

    /**
     * token保存方式
     *
     * @return
     */
    @Bean
    public InMemoryTokenStore inMemoryTokenStore() {
        return new InMemoryTokenStore();
    }

    /**
     * 授权码保存方式
     *
     * @return
     */
    @Bean
    public InMemoryAuthorizationCodeServices inMemoryAuthorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
}
