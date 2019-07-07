package Aop;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class MyAnnotationAwareAspectJAutoProxyCreator extends AspectJAwareAdvisorAutoProxyCreator{
    @Nullable
    private List<Pattern> includePatterns;
    @Nullable
    private AspectJAdvisorFactory aspectJAdvisorFactory;
    @Nullable
    private BeanFactoryAspectJAdvisorsBuilder aspectJAdvisorsBuilder;

    private Environment environment;

    public MyAnnotationAwareAspectJAutoProxyCreator(Environment environment) {
        this.environment=environment;
    }

    public void setIncludePatterns(List<String> patterns) {
        this.includePatterns = new ArrayList(patterns.size());
        Iterator var2 = patterns.iterator();

        while(var2.hasNext()) {
            String patternText = (String)var2.next();
            this.includePatterns.add(Pattern.compile(patternText));
        }

    }

    public void setAspectJAdvisorFactory(AspectJAdvisorFactory aspectJAdvisorFactory) {
        Assert.notNull(aspectJAdvisorFactory, "AspectJAdvisorFactory must not be null");
        this.aspectJAdvisorFactory = aspectJAdvisorFactory;
    }

    protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.initBeanFactory(beanFactory);
        if (this.aspectJAdvisorFactory == null) {
            this.aspectJAdvisorFactory = new MyReflectiveAspectJAdvisorFactory(beanFactory,environment);
        }

        this.aspectJAdvisorsBuilder = new MyAnnotationAwareAspectJAutoProxyCreator.BeanFactoryAspectJAdvisorsBuilderAdapter(beanFactory, this.aspectJAdvisorFactory);
    }

    protected List<Advisor> findCandidateAdvisors() {
        List<Advisor> advisors = super.findCandidateAdvisors();
        if (this.aspectJAdvisorsBuilder != null) {
            advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
        }

        return advisors;
    }

    protected boolean isInfrastructureClass(Class<?> beanClass) {
        return super.isInfrastructureClass(beanClass) || this.aspectJAdvisorFactory != null && this.aspectJAdvisorFactory.isAspect(beanClass);
    }

    protected boolean isEligibleAspectBean(String beanName) {
        if (this.includePatterns == null) {
            return true;
        } else {
            Iterator var2 = this.includePatterns.iterator();

            Pattern pattern;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                pattern = (Pattern)var2.next();
            } while(!pattern.matcher(beanName).matches());

            return true;
        }
    }

    private class BeanFactoryAspectJAdvisorsBuilderAdapter extends BeanFactoryAspectJAdvisorsBuilder {
        public BeanFactoryAspectJAdvisorsBuilderAdapter(ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
            super(beanFactory, advisorFactory);
        }

        protected boolean isEligibleBean(String beanName) {
            return MyAnnotationAwareAspectJAutoProxyCreator.this.isEligibleAspectBean(beanName);
        }
    }

}
