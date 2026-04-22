package com.example.gmaildemo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendEmailRequest {

    private String from;
    private String to;
    private String subject;
    private String body;

}