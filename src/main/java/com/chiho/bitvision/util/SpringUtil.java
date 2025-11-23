package com.chiho.bitvision.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 核心作用是提供对Spring IoC容器的全局访问能力，使得在非Spring管理的类中也能获取和使用Spring容器中的Bean
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    //获取applicationContext
    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    //通过name获取 Bean.
    public static Object getBean(String beanName){
        return getApplicationContext().getBean(beanName);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> beanClass){
        return getApplicationContext().getBean(beanClass);
    }

    //通过beanName,以及beanClass返回指定的Bean
    public static <T> T getBean(String beanName, Class<T> beanClass){
        return getApplicationContext().getBean(beanName, beanClass);
    }
}
