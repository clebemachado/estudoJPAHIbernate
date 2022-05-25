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
        manager.persist(cart);

        i1.setCart(cart);
        i2.setCart(cart);
        i3.setCart(cart);

        manager.persist(i1);
        manager.persist(i2);
        manager.persist(i3);

        manager.getTransaction().commit();
        manager.close();
    }
}
