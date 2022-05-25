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
        manager.persist(address);
        manager.persist(users);
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
