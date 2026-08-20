package com.example.springdemo.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public record UserDto(
           // used to ignore the immediate feild
           // @JsonIgnore

           // for renaming feild
           // @JsonProperty("user_id")
            Long id,
            String name,
            String email

//            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//            LocalDateTime createdAt
)
{

}
