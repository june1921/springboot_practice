package com.ggoreb.practice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity @Data
public class FileAtch {
    @Id
    @GeneratedValue

    Long id;
    String fileName; 

    @OneToOne
    Question question;
    
}
