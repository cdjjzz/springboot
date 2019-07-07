package Aop;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            RootBeanDefinition apcDefinition = (RootBeanDefinition) registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            apcDefinition.setBeanClass(MyAnnotationAwareAspectJAutoProxyCreator.class);
        }else{
            RootBeanDefinition beanDefinition = new RootBeanDefinition(MyAnnotationAwareAspectJAutoProxyCreator.class);
            beanDefinition.setSource((Object)null);
            beanDefinition.getPropertyValues().add("order", -2147483648);
            beanDefinition.setRole(2);
            registry.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator",
                    beanDefinition);
        }
    }

}
