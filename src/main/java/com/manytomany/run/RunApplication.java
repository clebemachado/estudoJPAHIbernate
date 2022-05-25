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
