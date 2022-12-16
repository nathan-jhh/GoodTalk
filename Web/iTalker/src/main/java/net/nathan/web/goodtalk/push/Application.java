package net.nathan.web.goodtalk.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.nathan.web.goodtalk.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig{
    public Application(){
        // 注册逻辑处理包名
        packages(AccountService.class.getPackage().getName());

        // 注册json解析器
        register(JacksonJsonProvider.class);

        register(Logger.class);
    }
}
