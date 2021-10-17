package com.notkamui.event;

import com.notkamui.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "event")
public class Event {
    public Event() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = {CascadeType.ALL})
    private User user;

    @NotNull
    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "place")
    private String place;

    @NotNull
    @NotBlank
    @Column(name = "recur_rule_parts", nullable = false)
    private String recurRuleParts;
}
