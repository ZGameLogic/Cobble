package com.zgamelogic.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Beans {
    @Bean
    public ObjectMapper objectMapperBean(){
        return new ObjectMapper();
    }
}
