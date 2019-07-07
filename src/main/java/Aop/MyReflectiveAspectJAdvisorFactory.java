package Aop;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MyReflectiveAspectJAdvisorFactory extends ReflectiveAspectJAdvisorFactory  {

    private final BeanFactory beanFactory;


    private String methodName;

    private String classPath;

    private String classSimpleName;
    public Environment environment;


    public  MyReflectiveAspectJAdvisorFactory(BeanFactory beanFactory,Environment environment){
        this.beanFactory=beanFactory;
        this.environment=environment;
    }
    @Nullable
    public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrderInAspect, String aspectName) {
        this.validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());
        if(classPath==null||!classPath.equals(aspectInstanceFactory.getAspectMetadata().getAspectClass().getName())){
            List<Method> methods = new ArrayList();
            ReflectionUtils.doWithMethods(aspectInstanceFactory.getAspectMetadata().getAspectClass(), (method) -> {
                Pointcut pointcut=AnnotationUtils.getAnnotation(method, Pointcut.class);
                if(pointcut!=null&&StringUtils.isEmpty(pointcut.value())){
                    methodName=method.getName()+"()";
                }
            });
        }
        classPath=aspectInstanceFactory.getAspectMetadata().getAspectClass().getName();
        classSimpleName=aspectInstanceFactory.getAspectMetadata().getAspectClass().getSimpleName();
        AspectJExpressionPointcut expressionPointcut = this.getPointcut(candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
        return expressionPointcut == null ? null : new MyInstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod, this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
    }

    @Nullable
    private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
        AspectJAnnotation<?> aspectJAnnotation = MyReflectiveAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        } else {
            AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class[0]);

            if(aspectJAnnotation.getPointcutExpression().contains(methodName)){
                String ex=aspectJAnnotation.getPointcutExpression();
                String propertiesex=environment.getProperty("spring.autoread.expression."+classSimpleName);
                ex=ex.replace(methodName,propertiesex);
                ajexp.setExpression(ex);
            }else{
                ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
            }
            if (this.beanFactory != null) {
                ajexp.setBeanFactory(this.beanFactory);
            }

            return ajexp;
        }
    }
}
