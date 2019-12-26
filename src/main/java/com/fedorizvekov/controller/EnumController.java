package com.fedorizvekov.controller;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import com.fedorizvekov.enums.DynamicEnum;
import com.fedorizvekov.service.DynamicCreationEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enums")
@RequiredArgsConstructor
public class EnumController {

    @Value("${dynamic.enums}")
    private String[] propertyEnums;


    @PostConstruct
    public void postConstruct() {
        for (String enumName : propertyEnums) {
            DynamicCreationEnum.addEnum(DynamicEnum.class, enumName);
        }
    }


    @PutMapping("/{newEnum}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void addEnum(@PathVariable String newEnum) {
        DynamicCreationEnum.addEnum(DynamicEnum.class, newEnum);
    }


    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String getEnums() {
        return Arrays.toString(DynamicEnum.values());
    }

}
