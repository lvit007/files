package org.hibernate.tutorial.annotations;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="Persons")
@Proxy(lazy = false)
@ToString @EqualsAndHashCode
//@Cacheable(value = true)@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
public class Person implements Serializable{
    static final long serialVersionUID = -7593775012501239455L;

    @Id() @GeneratedValue()
    @Column(name = "personId")
    private Long id;
    /*@Version @Getter @Setter
    @Column (name="version")
    private Integer version;*/
    @Column(name = "fName")
    private String firstName;
    @Column(name = "sName")
    private String sureName;
     @OneToMany(cascade =CascadeType.ALL //{CascadeType.PERSIST,CascadeType.MERGE,CascadeType.ALL}
            ,fetch = FetchType.EAGER,
            mappedBy = "person1")
    @Getter @Setter
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    //@Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
   // private Set<Event> events = new HashSet<Event>();
    private List<Event> events = new ArrayList<Event>();


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



}


