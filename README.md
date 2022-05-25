# JPA - Hibernate + Relations

### Dependências Maven

```java
<dependencies>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.6.9.Final</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.29</version>
    </dependency>
</dependencies>
```

### Configuação persistence.xml - mysql

```java
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1"
           xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
  <persistence-unit name="estudojpa" transaction-type="RESOURCE_LOCAL">
      <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
      <class><!-- Entity Manager Class Name --></class>
      <properties>
          <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
          <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/curso_java" />
          <property name="javax.persistence.jdbc.user" value="user" />
          <property name="javax.persistence.jdbc.password" value="password" />

          <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL57Dialect"/>
          <property name="hibernate.show_sql" value="true"/>
          <property name="hibernate.format_sql" value="true"/>
          <property name="hibernate.hbm2ddl.auto" value="update"/>

      </properties>
  </persistence-unit>
</persistence>
```

# One To One

![image](https://user-images.githubusercontent.com/66011013/170285024-769eee87-32f6-41ed-a04e-c15f2d5996b7.png)

### Class Users

- O JoinColumn é colocado na classe onde será salvo o ID da outra classe (ou classe principal).

```java
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "address_id", referencedColumnName = "id")
private Address address;
```

### Class Address

- Na classe principal, é utilizado o mappedBy para informar que as informações da classe já estão sendo  mapeadas na classe “secundária”

```java
@OneToOne(mappedBy = "address")
private Users users;
```

### Como salvar com o JPA

- Primeiro é salvo a parte do relacionamento “principal”
- Depois é salvo o relacionamento secundário, já que vai ter o ID já salvo no banco de dados

```java
package com.onetoone.run;

import com.onetoone.models.Address;
import com.onetoone.models.Users;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class RunApplication {
    public static void main(String[] args) {

        Address address = new Address();
        Users users = new Users();

        address.setCity("São Luís");
        address.setStreet("Rua das Flores");
        address.setUsers(users);

        users.setUserName("jj");
        users.setAddress(address);

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("estudojpa");
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(address); // Primeiro salva a classe pai
        manager.persist(users); // Depois salva a classe filho
        //var usersList = manager.createQuery("Select a from Users a", Users.class).getResultList();
        manager.getTransaction().commit();
        /*
        for(Users u: usersList){
            System.out.println(u);
        }
         */
        manager.close();
    }
}
```

# One To Many

![image](https://user-images.githubusercontent.com/66011013/170285214-6814face-07d9-4ad4-9c40-f7ab74e5c477.png)

### Class Cart

- É a parte do relacionamento ONE
- Terá um conjunto de dados de items  (SET, LIST, MAP and other)

```java
@OneToMany(mappedBy = "cart")
private Set<Items> items = new HashSet<>();
```

- Lembrando que coleções não são passados no construtor, e sim definidas por setters

```java
public Cart(UUID id) {
    this.id = id;
}

public void setItems(Set<Items> items) {
    this.items = items;
}
```

### Class Items

- É a parte do relacionamento dos muitos
- Vai levar o ID do cart

```java
@ManyToOne
@JoinColumn(name = "cart_id", referencedColumnName = "id", nullable = false)
private Cart cart;

public Items(){}

public Items(UUID id, Cart cart) {
    this.id = id;
    this.cart = cart;
}
```

### Como salvar com o JPA

- Primeiro salva a parte do relacionamento ONE
- Depois salva a parte do relacionamento MANY

```java
package com.onetomany.run;

import com.onetomany.models.Cart;
import com.onetomany.models.Items;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RunApplication {
    public static void main(String[] args) {
        
				EntityManagerFactory factory = Persistence.createEntityManagerFactory("estudojpa");
        EntityManager manager = factory.createEntityManager();

        Cart cart = new Cart();
        Items i1 = new Items();
        Items i2 = new Items();
        Items i3 = new Items();

        cart.setItems(new HashSet<>(Arrays.asList(i1, i2, i3)));

        manager.getTransaction().begin();

        manager.persist(cart); // Primeiro salva o ONE

        i1.setCart(cart);
        i2.setCart(cart);
        i3.setCart(cart);
				
				// DEPOIS SALVA OS MANY
        manager.persist(i1);
        manager.persist(i2);
        manager.persist(i3);

        manager.getTransaction().commit();
        manager.close();
    }
}
```

### 

# Many To Many

![image](https://user-images.githubusercontent.com/66011013/170285324-81df7717-3b8f-45c3-b7a7-a48b9e6b0399.png)

- Será criado uma tabela de junção com os ID da tabela 1 e  2
- Só uma das classes precisa criar a junção utilizando @JoinTable, com o nome da coluna e os ids respectivos para Junção

### Course

- Terá uma coleção de student

```java
@ManyToMany(mappedBy = "likedCourses")
private Set<Student> likes = new HashSet<>();

public Course(){}

public Course(UUID id) {
    this.id = id;
}
```

### Student

- Terá uma coleção de course

```java
@ManyToMany
@JoinTable(
        name = "courses_like",
        joinColumns = @JoinColumn(name="student_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id")
)
private Set<Course> likedCourses = new HashSet<>();

public Student(){}

public Student(UUID id) {
    this.id = id;
}
```

### Como salvar com o JPA

- Primeiro salva as instâncias sem ter adicionado as collections.
- Depois adiciona as collections e persist a instância que tem a joinTable

```java
package com.manytomany.run;

import com.manytomany.models.Course;
import com.manytomany.models.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.HashSet;

public class RunApplication {
    public static void main(String[] args) {
        Student s1 = new Student();
        Student s2 = new Student();

        Course c1 = new Course();
        Course c2 = new Course();

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("estudojpa");
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        manager.persist(s1); 
        manager.persist(s2);
        manager.persist(c1);
        manager.persist(c2);

        s1.setLikedCourses(new HashSet<>(Arrays.asList(c1,c2)));
        s2.setLikedCourses(new HashSet<>(Arrays.asList(c1)));

        manager.persist(s1);
        manager.persist(s2);

        manager.getTransaction().commit();
        manager.close();

    }
}
```
