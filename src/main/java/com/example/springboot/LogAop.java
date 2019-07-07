package com.example.springboot;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAop {

    @Pointcut()
    public void webLog(){

    }

    @Before("webLog()")
    public void performance(){
        System.out.println("Spring1 before AOP");
    }

    @After("webLog()")
    public void afterrmance(){
        System.out.println("Spring1 after AOP");
    }



}
