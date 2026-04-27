package com.wotiwan.medonline;

import com.wotiwan.medonline.database.entity.Role;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MedOnlineApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(MedOnlineApplication.class, args);
    }
}
