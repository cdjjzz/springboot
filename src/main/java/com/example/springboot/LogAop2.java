package com.example.springboot;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAop2 {

    @Pointcut()
    public void webLog2(){

    }

    @Before("webLog2()")
    public void performance(){
        System.out.println("Spring2 before AOP");
    }

    @After("webLog2()")
    public void afterrmance(){
        System.out.println("Spring2 after AOP");
    }



}
