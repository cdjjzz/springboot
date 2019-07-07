package Aop;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJPrecedenceInformation;
import org.springframework.aop.aspectj.InstantiationModelAwarePointcutAdvisor;
import org.springframework.aop.aspectj.annotation.*;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.aop.support.Pointcuts;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

public class MyInstantiationModelAwarePointcutAdvisorImpl extends AbstractAspectJAdvisorFactory implements InstantiationModelAwarePointcutAdvisor, AspectJPrecedenceInformation, Serializable {





    private static final Advice EMPTY_ADVICE = new Advice() {
    };
    private final AspectJExpressionPointcut declaredPointcut;
    private final Class<?> declaringClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private transient Method aspectJAdviceMethod;
    private final AspectJAdvisorFactory aspectJAdvisorFactory;
    private final MetadataAwareAspectInstanceFactory aspectInstanceFactory;
    private final int declarationOrder;
    private final String aspectName;
    private final Pointcut pointcut;
    private final boolean lazy;
    @Nullable
    private Advice instantiatedAdvice;
    @Nullable
    private Boolean isBeforeAdvice;
    @Nullable
    private Boolean isAfterAdvice;

    public MyInstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut declaredPointcut, Method aspectJAdviceMethod, AspectJAdvisorFactory aspectJAdvisorFactory, MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
        this.declaredPointcut = declaredPointcut;
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectJAdvisorFactory = aspectJAdvisorFactory;
        this.aspectInstanceFactory = aspectInstanceFactory;
        this.declarationOrder = declarationOrder;
        this.aspectName = aspectName;
        if (aspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
            Pointcut preInstantiationPointcut = Pointcuts.union(aspectInstanceFactory.getAspectMetadata().getPerClausePointcut(), this.declaredPointcut);
            this.pointcut = new MyInstantiationModelAwarePointcutAdvisorImpl.PerTargetInstantiationModelPointcut(this.declaredPointcut, preInstantiationPointcut, aspectInstanceFactory);
            this.lazy = true;
        } else {
            this.pointcut = this.declaredPointcut;
            this.lazy = false;
            this.instantiatedAdvice = this.instantiateAdvice(this.declaredPointcut);
        }

    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public synchronized boolean isAdviceInstantiated() {
        return this.instantiatedAdvice != null;
    }

    public synchronized Advice getAdvice() {
        if (this.instantiatedAdvice == null) {
            this.instantiatedAdvice = this.instantiateAdvice(this.declaredPointcut);
        }

        return this.instantiatedAdvice;
    }

    private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
        Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut, this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
        return advice != null ? advice : EMPTY_ADVICE;
    }

    public boolean isPerInstance() {
        return this.getAspectMetadata().getAjType().getPerClause().getKind() != PerClauseKind.SINGLETON;
    }

    public AspectMetadata getAspectMetadata() {
        return this.aspectInstanceFactory.getAspectMetadata();
    }

    public MetadataAwareAspectInstanceFactory getAspectInstanceFactory() {
        return this.aspectInstanceFactory;
    }

    public AspectJExpressionPointcut getDeclaredPointcut() {
        return this.declaredPointcut;
    }

    public int getOrder() {
        return this.aspectInstanceFactory.getOrder();
    }

    public String getAspectName() {
        return this.aspectName;
    }

    public int getDeclarationOrder() {
        return this.declarationOrder;
    }

    public boolean isBeforeAdvice() {
        if (this.isBeforeAdvice == null) {
            this.determineAdviceType();
        }

        return this.isBeforeAdvice;
    }

    public boolean isAfterAdvice() {
        if (this.isAfterAdvice == null) {
            this.determineAdviceType();
        }

        return this.isAfterAdvice;
    }

    private void determineAdviceType() {
        AspectJAnnotation<?> aspectJAnnotation = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(this.aspectJAdviceMethod);
        if (aspectJAnnotation == null) {
            this.isBeforeAdvice = false;
            this.isAfterAdvice = false;
        } else {
            switch(aspectJAnnotation.getAnnotationType()) {
                case AtPointcut:
                case AtAround:
                    this.isBeforeAdvice = false;
                    this.isAfterAdvice = false;
                    break;
                case AtBefore:
                    this.isBeforeAdvice = true;
                    this.isAfterAdvice = false;
                    break;
                case AtAfter:
                case AtAfterReturning:
                case AtAfterThrowing:
                    this.isBeforeAdvice = false;
                    this.isAfterAdvice = true;
            }
        }

    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();

        try {
            this.aspectJAdviceMethod = this.declaringClass.getMethod(this.methodName, this.parameterTypes);
        } catch (NoSuchMethodException var3) {
            throw new IllegalStateException("Failed to find advice method on deserialization", var3);
        }
    }

    public String toString() {
        return "InstantiationModelAwarePointcutAdvisor: expression [" + this.getDeclaredPointcut().getExpression() + "]; advice method [" + this.aspectJAdviceMethod + "]; perClauseKind=" + this.aspectInstanceFactory.getAspectMetadata().getAjType().getPerClause().getKind();
    }

    @Override
    public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory metadataAwareAspectInstanceFactory) {
        return null;
    }

    @Override
    public Advisor getAdvisor(Method method, MetadataAwareAspectInstanceFactory metadataAwareAspectInstanceFactory, int i, String s) {
        return null;
    }

    @Override
    public Advice getAdvice(Method method, AspectJExpressionPointcut aspectJExpressionPointcut, MetadataAwareAspectInstanceFactory metadataAwareAspectInstanceFactory, int i, String s) {
        return null;
    }

    private final class PerTargetInstantiationModelPointcut extends DynamicMethodMatcherPointcut {
        private final AspectJExpressionPointcut declaredPointcut;
        private final Pointcut preInstantiationPointcut;
        @Nullable
        private LazySingletonAspectInstanceFactoryDecorator aspectInstanceFactory;

        public PerTargetInstantiationModelPointcut(AspectJExpressionPointcut declaredPointcut, Pointcut preInstantiationPointcut, MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
            this.declaredPointcut = declaredPointcut;
            this.preInstantiationPointcut = preInstantiationPointcut;
            if (aspectInstanceFactory instanceof LazySingletonAspectInstanceFactoryDecorator) {
                this.aspectInstanceFactory = (LazySingletonAspectInstanceFactoryDecorator)aspectInstanceFactory;
            }

        }

        public boolean matches(Method method, Class<?> targetClass) {
            return this.isAspectMaterialized() && this.declaredPointcut.matches(method, targetClass) || this.preInstantiationPointcut.getMethodMatcher().matches(method, targetClass);
        }

        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return this.isAspectMaterialized() && this.declaredPointcut.matches(method, targetClass);
        }

        private boolean isAspectMaterialized() {
            return this.aspectInstanceFactory == null || this.aspectInstanceFactory.isMaterialized();
        }
    }
}
