package com.devops.puissance4.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("puissance4PU");

    private JpaUtil() {
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}