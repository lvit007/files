package org.hibernate.tutorial.annotations;


import java.util.*;

import static junit.framework.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.junit.*;
import org.junit.Test;

@Slf4j
public class AnnotationsIllustrationTest {
    private SessionFactory sessionFactory;

    @Before
    public void setUp() {
        // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
    }

    @After
    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @Ignore
    public void testBasicUsage() {
        //transient object
        Person person = new Person();
        person.setFirstName("Vit");
        person.setSureName("Lopanov");
        /*Event event1 = new Event("Our very first event!", new Date());
        Event event2 = new Event("A follow up event", new Date());
        event1.setPerson1(person);
        event2.setPerson1(person);
        List<Event> events = new ArrayList<Event>();
        events.add(event1);
        events.add(event2);
        person.setEvents(events); */

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        //persistent object
        session.saveOrUpdate(person);
        session.getTransaction().commit();
        session.close();
        //detached object  объект person перешел в состояние detached
        /*
        session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Person p").list();
        for (Person person1 : (List<Person>) result) {
            log.info("Event (" + person1.getFirstName() + ") : " + person1.getSureName() + " \n"
                    + person1.getEvents().toString());
        }
        Person person2 = (Person) session.get(Person.class, 1L);
        //removed - объект
        session.delete(person2);
        result = session.createQuery("from Event").list();
        for (Event event : (List<Event>) result) {
            log.info(event.toString());
            event.getPerson1().toString();
        }
        session.getTransaction().commit();
        session.close();*/
    }

    @Test()
    @Ignore
    public void loadProxyPersons() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        // в бд нет записи 10L load()  возвращает proxy  объект
        Person person2 = (Person) session.load(Person.class, 10L);
        //проверяем существование proxy  объекта
        assertNotNull(person2);
        session.getTransaction().commit();
        session.close();
    }

    @Ignore
    @Test(expected = org.hibernate.ObjectNotFoundException.class)
    public void loadProxyPersonsException() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        // в бд нет записи 10L load()  возвращает proxy  объект
        Person person2 = (Person) session.load(Person.class, 10L);
        // вызываем исключение  org.hibernate.ObjectNotFoundException.class
        person2.getFirstName();
        session.getTransaction().commit();
        session.close();
    }

    @Test()
    @Ignore
    public void getNullPerson() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        //запращиваем объект
        Person person2 = (Person) session.get(Person.class, 10L);
        //проверяем что get возвращает null
        assertNull(person2);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @Ignore
    public void testHibernateCacheLevelOne() {
        Person person = new Person();
        person.setFirstName("Vit");
        person.setSureName("Lopanov");
        Statistics statistics = sessionFactory.getStatistics();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(person);
        session.getTransaction().commit();
        session.close();
        //включаем статистику для сбора информации.
        statistics.setStatisticsEnabled(true);
        session = sessionFactory.openSession();
        session.beginTransaction();
        // загружаем объект из БД - выполняется sql:
        //Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
        person = (Person) session.load(Person.class, 1L);
        // проверяем сохранен ли наш объект в кеше -  true
        System.out.println("contains in cache: " + session.contains(person));
        person.setFirstName("Tom");
        session.saveOrUpdate(person);
        //.... выполняем еще какие-то операции
        //запрашиваем заново наш объект, sql не выполняется, объект подсовывается из кеша
        person = (Person) session.load(Person.class, 1L);
        // проверяем находиться ли наш объект в кеше -  true
        System.out.println("contains in cache: " + session.contains(person));
        person.setSureName("Aristov");
        session.saveOrUpdate(person);
        // выгружаем объект из кеша
        if (session.contains(person)) session.evict(person);
        // объект не найден в кеше, выполняем sql запрос
        //Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
        person = (Person) session.load(Person.class, 1L);
        person.setFirstName("Oleg");
        session.flush();
        session.saveOrUpdate(person);
        session.getTransaction().commit();
        session.close();
        System.out.println("InsertCount: " + statistics.getEntityInsertCount() + " " +
                "UpdateCount: " + statistics.getEntityUpdateCount() + " " +
                "FlushCount: " + statistics.getFlushCount() + " " +
                "TransactionCount: " + statistics.getTransactionCount() + " " +
                "SuccessfulTransactionCount: " + statistics.getSuccessfulTransactionCount());
    }

    @Test  //@Ignore
    public void batchInsert() {
        Person person;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (int i = 0; i < 10000; i++) {
            person = new Person();
            person.setFirstName("Vit" + i);
            person.setSureName("Lopanov" + i);
            session.saveOrUpdate(person);
            if (i % 30 == 0) {
                //скидываем изменения
                session.flush();
                //очищаем кеш
                session.clear();
            }
        }
        session.getTransaction().commit();
        session.close();
    }


}
