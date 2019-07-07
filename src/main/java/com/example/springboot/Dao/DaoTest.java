package com.example.springboot.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/dao")
public class DaoTest {


    @GetMapping("/bu")
    public String  bu(){
        System.out.println("asdasd");
        return "罗盛丰bu";
    }
    @GetMapping("/du")
    public String  du(){
        System.out.println("asdasd");
        return "罗盛丰du";
    }

}
