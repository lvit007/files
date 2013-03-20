Hibernate интересные моменты.
Замапленый объект может находиться в четырех состояниях:

1.transient-объект создаётся командой new, может быть заполнен данными но,
 ни когда не сохранялся в сессии т.е. не ассоциирован со строкой в таблице БД. 

2.persistent-объект, объект в данный момент связанный с некоторой сессией,
hibernate сессия "работает" с экземпляром объекта в данный промежуток времени,
т.е. объект ассоциирован со строкой в таблице БД. Получить persistent-объект можно
двумя способами:
1-й способ запросить объект такими методами как get(), load(),  list(),
 uniqueResult(), iterate(), scroll(), find(), getReference().
2-й способ перевести наш transient-объект в persistent-объект путем вызова таких
методов как save(), saveOrUpdate(), persist(), merge(). 
 
3. detached-объект - это persistent-объект отсоединенный от сессии, это состояние объекта возникает 
после закрытия сессии close(), которая работала с объектом до этого или при вызовах
методов evict(), clear() сессии. Переход из состояния detached в persistent объектом
возможно при вызове методов сессии - update(), saveOrUpdate(), merge().

4. removed - объект - это persistent-объект удаленный в сессии методом delete() или remove() в jpa. 


Еще хотелось бы сказать пару слов о detached-объектах, их можно использовать 
дальше, при работе с новой сессией, если к ним применить такие команды как update(); saveOrUpdate() или
merge() (в jpa), то они переходят в состояние persistent - в Hibernate такие операции
называются reattached mode или merging mode - в jpa. 

Замапленный объект должен удовлетворять требованиям, тогда и только тогда 
он будет правильно взаимодействовать с hibernate:
1. Иметь конструктор по умолчанию без параметров.
2. Переопределять методы toString(), Equals(), HashCode().
3. Иметь поле "первичного ключа" (id) как минимум, как максимум сложный составной ключ.
4. Класс сущности не должен быть объявлен как final класс.
5. Класс сущности должен наследовать интерфейс Serializable.


Немножко отступим от темы и поговорим о Serializable. 
С каждым сериализуемым классом связан уникальный идентификационный номер.
Если вы не указываете этот идентификатор явно, декларируя поле 
private static final long с названием serialVersionUID, система генерирует его автоматически,
 используя для класса сложную схему расчетов. При этом на автоматически генерируемое значение
 оказывают влияние название класса,
названия реализуемых им интерфейсов, а также все открытые и защищенные члены. Если вы каким-то
образом поменяете что-либо в этом наборе, например, добавите простой и удобный метод, изменится
и автоматически генерируемый serial version UID. Следовательно, если вы не будете явным образом
декларировать этот идентификатор, совместимость с предыдущими версиями будет потеряна.
Клиенты, которые пытаются сериализовать объект с
помощью старой версии класса и десериализовать его уже с помощью новой версии, получат сбой
программы. И так, как создать static final long serialVersionUID? Пусть у нас есть
скомпилированный класс src/org/hibernate/tutorial/annotations/Person.class
перейдем в папку src и запустим команду serialver которая входит в jdk:

C:\...\src>serialver org.hibernate.tutorial.annotations.Person
org.hibernate.tutorial.annotations.Person:
static final long serialVersionUID = -7593775012501239455L;
C:\...\src>
копируем и вставляем в наш класс:

public class Person implements Serializable{
    static final long serialVersionUID = -7593775012501239455L;
...
}

Различие между командами load() и get(), оба метода предназначены для
получения объекта из базы данных, отличие в том что если метод get() не 
находит объект в БД то возвращает null,  метод load() может вернуть прокси
объект (если разрещена реалиализация lazy объектов - по умолчанию в Hibernate 3),
вместо реального объекта. Заполнение данными proxy-объекта происходит, только
 после вызова любого метода прокси объекта исключение составляет запрос первичного ключа (getId()).
Hibernate выполняет sql запрос только тогда, когда нужны реальные данные,
если существует такая запись в БД то она заполняет данными proxy-объект.
 Все это называется ленивой загрузкой и её можно отменить выставив параметр
 @Proxy(lazy = false) у объекта. Если запись не найдена в БД, то происходит
 Exception (org.hibernate.ObjectNotFoundException.class). 
Смотрите коментарии в примерах:

    @Test()
    public void loadProxyPersons(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        //в бд нет записи 10L load()  возвращает proxy  объект
        Person person2 = (Person) session.load(Person.class, 10L);
        //проверяем существование proxy  объекта
        assertNotNull(person2);
        session.getTransaction().commit();
        session.close();
    }

    @Test(expected = org.hibernate.ObjectNotFoundException.class)
    public void loadProxyPersonsException(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        // в бд нет записи 10L load()  возвращает proxy  объект
        Person person2 = (Person) session.load(Person.class, 10L);
        //пытаемся заполнить проси объект данными, вызываем исключение  org.hibernate.ObjectNotFoundException.class
        person2.getFirstName();
        session.getTransaction().commit();
        session.close();
    }

    @Test()
    public void getNullPerson(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        //запращиваем объект
        Person person2 = (Person) session.get(Person.class, 10L);
        //проверяем что get возвращает null
        assertNull(person2);
        session.getTransaction().commit();
        session.close();
    }

По умолчанию Hibernate, всегда использует кеш первого уровня в транзакции, 
его не возможно отключить. Т.е когда мы производим операции над 
persistent-объектом изменения не сразу поподают в БД, это нужно 
для уменьшения количества выполняемых sql запросов к базе данных. 
Допустим объект модифицируется в одной сессии несколько раз,
все изменения происходят в памяти (aka кеше первого уровня), в итоге генерируется 1 update
 sql запрос который скидывает только последние изменения по последнему состоянию, 
все предыдушие состояния объекта не учитываються и не сохраняюстя в БД.
Расмотрим пример:

    @Test
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
        person = (Person)session.load(Person.class,1L);
        // проверяем сохранен ли наш объект в кеше -  true
        System.out.println("contains in cache: "+session.contains(person));
        person.setFirstName("Tom");
        session.saveOrUpdate(person);
        //.... выполняем еще какие-то операции
        //запрашиваем заново наш объект, sql не выполняется, объект подсовывается из кеша
        person = (Person)session.load(Person.class,1L);
        // проверяем находиться ли наш объект в кеше -  true
        System.out.println("contains in cache: "+session.contains(person));
        person.setSureName("Aristov");
        session.saveOrUpdate(person);
        // выгружаем объект из кеша
        if (session.contains(person)) session.evict(person);
        // объект не найден в кеше, выполняем sql запрос
        //Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
        person = (Person)session.load(Person.class,1L);
        person.setFirstName("Oleg");
        session.saveOrUpdate(person);
        session.getTransaction().commit();
        session.close();
        System.out.println("InsertCount: "+statistics.getEntityInsertCount()+" " +
                "UpdateCount: "+statistics.getEntityUpdateCount()+" " +
                "FlushCount: "+statistics.getFlushCount()+" " +
                "TransactionCount: "+statistics.getTransactionCount()+" " +
                "SuccessfulTransactionCount: "+statistics.getSuccessfulTransactionCount());
    }

этот пример даёт такой лог, обратите внимание что мы меняли состояние объекта 3 
раза (setSureName("Aristov"), setFirstName("Oleg") ... ), 
а sql - update  выполнился лишь один раз:

Hibernate: insert into Persons (personId, fName, sName) values (null, ?, ?)
contains in cache: true
Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
contains in cache: true
Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
Hibernate: update Persons set fName=?, sName=? where personId=?
InsertCount: 0 UpdateCount: 1 FlushCount: 1 TransactionCount: 1 SuccessfulTransactionCount: 1

Отсюда вывод что:
При использовании методов save(), update(), saveOrUpdate(), load(), get(), 
list(), iterate(), scroll() всегда будет задействован кеш первого уровня,
для того что бы немедлено сохранить объект в БД, после вызовов методов save(), update(), saveOrUpdate(),
 нужно выполнить команду session.flush(). А для непосредственной загрузки объекта 
из БД sql-м, методами load(), get(), list(), iterate(), scroll(), 
а не из кеша первого уровня, нужно вызвать команду session.evict(person) 
- удаления объекта из кеша. Так же сушествует команда которая очищает полностью
кеш первого уровня session.clear();
 
Продолжая тему кеша первого уровня, нельзя не упамянуть про Batch Inserts,
загрузку множества объектов, для этого нужно контролировать кеш первого уровня
от разростания и вовремя скидывать данные в БД, очищая при этом кеш:

    @Test  
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
                //скидываем изменения из кеша первого уровня
                session.flush();
                //очищаем кеш первого уровня
                session.clear();
            }
        }
        session.getTransaction().commit();
        session.close();
    }

Не забудте установить параметр hibernate.jdbc.batch_size и отключить 
кеш второго уровня (second-level cache).









