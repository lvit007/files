/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tutorial.annotations;

import java.io.Serializable;
import java.util.*;

import junit.framework.TestCase;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

@Slf4j
public class AnnotationsIllustrationTest extends TestCase {
    private SessionFactory sessionFactory;

    @Override
    protected void setUp() throws Exception {
        // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
    }

    @Override
    protected void tearDown() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @SuppressWarnings({"unchecked"})
    public void testBasicUsage() {
        // create a couple of events...
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Person person = new Person();
        person.setFirstName("Vit");
        person.setSureName("Lopanov");
        Event event1 = new Event("Our very first event!", new Date());
        Event event2 = new Event("A follow up event", new Date());
        //session.save(person); person.getId(); //session.flush();
        event1.setPerson1(person);
        event2.setPerson1(person);
        //Set<Event> events  = new HashSet<Event>();
        List<Event> events = new ArrayList<Event>();
        events.add(event1);
        events.add(event2);
        person.setEvents(events);
        session.saveOrUpdate(person); //session.saveOrUpdate(event1);
        //session.save( new Event( "A follow up event", new Date() ) );
        session.getTransaction().commit();
        session.close();

        //now lets pull events from the database and list them
        session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery(
                //"from Person as p" ).setCacheable(true).list();
                "from Person as p join fetch p.events").setCacheable(true).list();
        Person person1;
        person1 = (Person) result.get(0);
        System.out.println("Event (" + person1.getFirstName() + ") : " + person.getSureName() + " \n" +
                person1.getEvents().toString());
        //}
        session.getTransaction().commit();
        session.close();

        Session session1 = sessionFactory.openSession();
        //session1.setCacheMode(CacheMode.NORMAL);
        session1.beginTransaction();
        /*log.info(String.valueOf(
                session1.getSessionFactory().getCache().containsQuery("from Person as p")));*/

        List result2 = session1.createQuery("from Person as p join fetch p.events").
                setCacheable(true).list();
        //List result2 = session1.load(Person.class,1L);
        /*for ( Person person1 : (List<Person>)result.get(0)) {*/

        Person person2;
        person2 = (Person) result2.get(0);
        log.info(String.valueOf(
                session1.getSessionFactory().getCache().containsEntity(Person.class, 1L)));
        log.info(String.valueOf(
                session1.getSessionFactory().getCache().
                        containsCollection("org.hibernate.tutorial.annotations.Person.events", 1L)));
        System.out.println("Event (" + person2.getFirstName() + ") : " + person2.getSureName() + " \n" +
                person2.getEvents().toString());
        //}
        session1.getTransaction().commit();
        session1.close();
        log.info(System.getProperty("java.io.tmpdir"));
        session1 = sessionFactory.openSession();
        session1.beginTransaction();
        ScrollableResults books = session1.createQuery("from Person as p join fetch p.events")
                //.setCacheMode(CacheMode.IGNORE)
                //.setCacheable(true)
                .scroll(ScrollMode.FORWARD_ONLY);
         books.next(); person2 = (Person)books.get(0);
        log.info(String.valueOf(
                session1.getSessionFactory().getCache().containsEntity(Person.class, 1L)));
        log.info(String.valueOf(
                session1.getSessionFactory().getCache().
                        containsCollection("org.hibernate.tutorial.annotations.Person.events", 1L)));
        System.out.println("Event (" + person2.getFirstName() + ") : " + person2.getSureName() + " \n" +
                person2.getEvents().toString());
        //}
        session1.getTransaction().commit();
        statistics.getSessionOpenCount();
        statistics.logSummary();
        session1.close();

    }
}
