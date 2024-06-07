package io.cc.gateway;

import io.cc.gateway.filter.WebClientHttpRoutingFilter;
import io.cc.gateway.handler.FilteringWebHandler;
import io.cc.gateway.handler.PluginWebHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author nhsoft.lsd
 */
@Component
public class WebHandlerBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor  {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        //TODO nhsoft.lsd 初始化 FilteringWebHandler or PluginWebHandler
        final String webHandler = "webHandler";
        final String engineHandler = PluginWebHandler.NAME;

        if (registry.containsBeanDefinition(webHandler) && registry.containsBeanDefinition(engineHandler)) {
            registry.removeBeanDefinition(webHandler);
            registry.registerBeanDefinition(webHandler, registry.getBeanDefinition(engineHandler));
            registry.removeBeanDefinition(engineHandler);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
