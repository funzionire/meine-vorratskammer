/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import model.AppUser;
import model.Household;

/**
 *
 * @author Felix
 */
@Stateless(name = "SessionBeanUser")
public class SessionBeanUser implements SessionBeanUserLocal {

    private static final Logger LOG = Logger.getLogger(SessionBeanUser.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Override
    public AppUser createUser(String name, String email, String password) {
        LOG.info("CustomInfo: SessionBean aufgerufen: User anlegen");
        em.setFlushMode(FlushModeType.AUTO);
        AppUser user = new AppUser(name, email, password);
        em.persist(user);
        user = em.merge(user);
        em.flush();
        return user;

    }

    @Override
    public AppUser login(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return null;
        }
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByEmailPassword", AppUser.class)
                .setParameter("email", email)
                .setParameter("password", password);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        return query.getSingleResult();
    }

    @Override
    public boolean deleteUser(AppUser user) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            em.remove(user);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*@Override
    public AppUser changeUser(AppUser user, String name, String email, String password) {
        em.setFlushMode(FlushModeType.AUTO);
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (password != null) {
            user.setPassword(password);
        }
        em.persist(user);
        user = em.merge(user);
        em.flush();
        return user;
    }*/

    @Override
    public boolean addHouseholdToUser(AppUser user, Household household) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            //???weitere Prüfung notwendig
            if (household != null) {
                user.getHouseholdList().add(household);
            }
            em.merge(user);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeHouseholdFromUser(AppUser user, Household household) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            if (household != null) {
                user.getHouseholdList().remove(household);
            }
            user = em.merge(user);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public AppUser changeName(AppUser user, String name) {
        em.setFlushMode(FlushModeType.AUTO);
        user.setName(name);
        user = em.merge(user);
        em.flush();
        return user;
    }

    @Override
    public AppUser changePassword(AppUser user, String password) {
        em.setFlushMode(FlushModeType.AUTO);
        user.setPassword(password);
        user = em.merge(user);
        em.flush();
        return user;
    }

    @Override
    public AppUser changeEmail(AppUser user, String email) {
        em.setFlushMode(FlushModeType.AUTO);
        user.setEmail(email);
        user = em.merge(user);
        em.flush();
        return user;
    }
    @Override
    public AppUser findUser(long longID) {
        LOG.info("lalelu" + longID);
        if (longID == 0) {
            return null;
        }
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findById", AppUser.class)
                .setParameter("userID", longID);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        LOG.info(query.getSingleResult().getName());
        return query.getSingleResult();
    }
    
    @Override
    public AppUser findUser(String email) {
        LOG.info("lalelu" + email);
        if (email == null) {
            return null;
        }
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByEmail", AppUser.class)
                .setParameter("email", email);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        LOG.info(query.getSingleResult().getName());
        return query.getSingleResult();
    }
}
