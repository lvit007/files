package org.hibernate.tutorial.annotations;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Persons")
@Proxy(lazy = false)
@ToString @EqualsAndHashCode
//@Cacheable(value = true)@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Person implements Serializable{
    @Id() @GeneratedValue()
    @Column(name = "personId")
    private Long id;
    @Column(name = "fName")
    private String firstName;
    @Column(name = "sName")
    private String sureName;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="Event_PersonId",referencedColumnName = "personId")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private Set<Event> events = new HashSet<Event>();

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

}


