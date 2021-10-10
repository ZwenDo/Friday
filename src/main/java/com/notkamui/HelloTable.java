package com.notkamui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hello")
public class HelloTable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
}
