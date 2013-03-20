Hibernate ���������� �������.
���������� ������ ����� ���������� � ������� ����������:

1.transient-������ �������� �������� new, ����� ���� �������� ������� ��,
 �� ����� �� ���������� � ������ �.�. �� ������������ �� ������� � ������� ��. 

2.persistent-������, ������ � ������ ������ ��������� � ��������� �������,
hibernate ������ "��������" � ����������� ������� � ������ ���������� �������,
�.�. ������ ������������ �� ������� � ������� ��. �������� persistent-������ �����
����� ���������:
1-� ������ ��������� ������ ������ �������� ��� get(), load(),  list(),
 uniqueResult(), iterate(), scroll(), find(), getReference().
2-� ������ ��������� ��� transient-������ � persistent-������ ����� ������ �����
������� ��� save(), saveOrUpdate(), persist(), merge(). 
 
3. detached-������ - ��� persistent-������ ������������� �� ������, ��� ��������� ������� ��������� 
����� �������� ������ close(), ������� �������� � �������� �� ����� ��� ��� �������
������� evict(), clear() ������. ������� �� ��������� detached � persistent ��������
�������� ��� ������ ������� ������ - update(), saveOrUpdate(), merge().

4. removed - ������ - ��� persistent-������ ��������� � ������ ������� delete() ��� remove() � jpa. 


��� �������� �� ������� ���� ���� � detached-��������, �� ����� ������������ 
������, ��� ������ � ����� �������, ���� � ��� ��������� ����� ������� ��� update(); saveOrUpdate() ���
merge() (� jpa), �� ��� ��������� � ��������� persistent - � Hibernate ����� ��������
���������� reattached mode ��� merging mode - � jpa. 

����������� ������ ������ ������������� �����������, ����� � ������ ����� 
�� ����� ��������� ����������������� � hibernate:
1. ����� ����������� �� ��������� ��� ����������.
2. �������������� ������ toString(), Equals(), HashCode().
3. ����� ���� "���������� �����" (id) ��� �������, ��� �������� ������� ��������� ����.
4. ����� �������� �� ������ ���� �������� ��� final �����.
5. ����� �������� ������ ����������� ��������� Serializable.


�������� �������� �� ���� � ��������� � Serializable. 
� ������ ������������� ������� ������ ���������� ����������������� �����.
���� �� �� ���������� ���� ������������� ����, ���������� ���� 
private static final long � ��������� serialVersionUID, ������� ���������� ��� �������������,
 ��������� ��� ������ ������� ����� ��������. ��� ���� �� ������������� ������������ ��������
 ��������� ������� �������� ������,
�������� ����������� �� �����������, � ����� ��� �������� � ���������� �����. ���� �� �����-��
������� ��������� ���-���� � ���� ������, ��������, �������� ������� � ������� �����, ���������
� ������������� ������������ serial version UID. �������������, ���� �� �� ������ ����� �������
������������� ���� �������������, ������������� � ����������� �������� ����� ��������.
�������, ������� �������� ������������� ������ �
������� ������ ������ ������ � ��������������� ��� ��� � ������� ����� ������, ������� ����
���������. � ���, ��� ������� static final long serialVersionUID? ����� � ��� ����
���������������� ����� src/org/hibernate/tutorial/annotations/Person.class
�������� � ����� src � �������� ������� serialver ������� ������ � jdk:

C:\...\src>serialver org.hibernate.tutorial.annotations.Person
org.hibernate.tutorial.annotations.Person:
static final long serialVersionUID = -7593775012501239455L;
C:\...\src>
�������� � ��������� � ��� �����:

public class Person implements Serializable{
    static final long serialVersionUID = -7593775012501239455L;
...
}

�������� ����� ��������� load() � get(), ��� ������ ������������� ���
��������� ������� �� ���� ������, ������� � ��� ��� ���� ����� get() �� 
������� ������ � �� �� ���������� null,  ����� load() ����� ������� ������
������ (���� ��������� ������������� lazy �������� - �� ��������� � Hibernate 3),
������ ��������� �������. ���������� ������� proxy-������� ����������, ������
 ����� ������ ������ ������ ������ ������� ���������� ���������� ������ ���������� ����� (getId()).
Hibernate ��������� sql ������ ������ �����, ����� ����� �������� ������,
���� ���������� ����� ������ � �� �� ��� ��������� ������� proxy-������.
 ��� ��� ���������� ������� ��������� � � ����� �������� �������� ��������
 @Proxy(lazy = false) � �������. ���� ������ �� ������� � ��, �� ����������
 Exception (org.hibernate.ObjectNotFoundException.class). 
�������� ���������� � ��������:

    @Test()
    public void loadProxyPersons(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        //� �� ��� ������ 10L load()  ���������� proxy  ������
        Person person2 = (Person) session.load(Person.class, 10L);
        //��������� ������������� proxy  �������
        assertNotNull(person2);
        session.getTransaction().commit();
        session.close();
    }

    @Test(expected = org.hibernate.ObjectNotFoundException.class)
    public void loadProxyPersonsException(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        // � �� ��� ������ 10L load()  ���������� proxy  ������
        Person person2 = (Person) session.load(Person.class, 10L);
        //�������� ��������� ����� ������ �������, �������� ����������  org.hibernate.ObjectNotFoundException.class
        person2.getFirstName();
        session.getTransaction().commit();
        session.close();
    }

    @Test()
    public void getNullPerson(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        //����������� ������
        Person person2 = (Person) session.get(Person.class, 10L);
        //��������� ��� get ���������� null
        assertNull(person2);
        session.getTransaction().commit();
        session.close();
    }

�� ��������� Hibernate, ������ ���������� ��� ������� ������ � ����������, 
��� �� �������� ���������. �.� ����� �� ���������� �������� ��� 
persistent-�������� ��������� �� ����� �������� � ��, ��� ����� 
��� ���������� ���������� ����������� sql �������� � ���� ������. 
�������� ������ �������������� � ����� ������ ��������� ���,
��� ��������� ���������� � ������ (aka ���� ������� ������), � ����� ������������ 1 update
 sql ������ ������� ��������� ������ ��������� ��������� �� ���������� ���������, 
��� ���������� ��������� ������� �� ������������ � �� ����������� � ��.
��������� ������:

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
        //�������� ���������� ��� ����� ����������.
        statistics.setStatisticsEnabled(true);
        session = sessionFactory.openSession();
        session.beginTransaction();
        // ��������� ������ �� �� - ����������� sql:
        //Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
        person = (Person)session.load(Person.class,1L);
        // ��������� �������� �� ��� ������ � ���� -  true
        System.out.println("contains in cache: "+session.contains(person));
        person.setFirstName("Tom");
        session.saveOrUpdate(person);
        //.... ��������� ��� �����-�� ��������
        //����������� ������ ��� ������, sql �� �����������, ������ ������������� �� ����
        person = (Person)session.load(Person.class,1L);
        // ��������� ���������� �� ��� ������ � ���� -  true
        System.out.println("contains in cache: "+session.contains(person));
        person.setSureName("Aristov");
        session.saveOrUpdate(person);
        // ��������� ������ �� ����
        if (session.contains(person)) session.evict(person);
        // ������ �� ������ � ����, ��������� sql ������
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

���� ������ ��� ����� ���, �������� �������� ��� �� ������ ��������� ������� 3 
���� (setSureName("Aristov"), setFirstName("Oleg") ... ), 
� sql - update  ���������� ���� ���� ���:

Hibernate: insert into Persons (personId, fName, sName) values (null, ?, ?)
contains in cache: true
Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
contains in cache: true
Hibernate: select person0_.personId as personId16_0_, person0_.fName as fName16_0_, person0_.sName as sName16_0_ from Persons person0_ where person0_.personId=?
Hibernate: update Persons set fName=?, sName=? where personId=?
InsertCount: 0 UpdateCount: 1 FlushCount: 1 TransactionCount: 1 SuccessfulTransactionCount: 1

������ ����� ���:
��� ������������� ������� save(), update(), saveOrUpdate(), load(), get(), 
list(), iterate(), scroll() ������ ����� ������������ ��� ������� ������,
��� ���� ��� �� ��������� ��������� ������ � ��, ����� ������� ������� save(), update(), saveOrUpdate(),
 ����� ��������� ������� session.flush(). � ��� ���������������� �������� ������� 
�� �� sql-�, �������� load(), get(), list(), iterate(), scroll(), 
� �� �� ���� ������� ������, ����� ������� ������� session.evict(person) 
- �������� ������� �� ����. ��� �� ���������� ������� ������� ������� ���������
��� ������� ������ session.clear();
 
��������� ���� ���� ������� ������, ������ �� ��������� ��� Batch Inserts,
�������� ��������� ��������, ��� ����� ����� �������������� ��� ������� ������
�� ����������� � ������� ��������� ������ � ��, ������ ��� ���� ���:

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
                //��������� ��������� �� ���� ������� ������
                session.flush();
                //������� ��� ������� ������
                session.clear();
            }
        }
        session.getTransaction().commit();
        session.close();
    }

�� ������� ���������� �������� hibernate.jdbc.batch_size � ��������� 
��� ������� ������ (second-level cache).









