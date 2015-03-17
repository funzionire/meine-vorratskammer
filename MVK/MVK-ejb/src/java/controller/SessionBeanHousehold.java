/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import model.Household;
import model.AppUser;
import model.Place;
import model.StocksArticle;

/**
 *
 * @author Felix
 */
@Stateless
public class SessionBeanHousehold implements SessionBeanHouseholdLocal {

    private static final Logger LOG = Logger.getLogger(SessionBeanHousehold.class.getName());
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Household createHousehold(String name) {
        em.setFlushMode(FlushModeType.AUTO);
        Household household = new Household(name);
        em.persist(household);
        household = em.merge(household);
        em.flush();
        return household;
    }

    @Override
    public boolean deleteHousehold(Household household) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            em.remove(household);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Household changeHousehold(Household household, String newName) {
        em.setFlushMode(FlushModeType.AUTO);
        if (newName != null) {
            household.setName(newName);
        }
        household = em.merge(household);
        em.flush();
        return household;
    }

    @Override
    public Household findHousehold(long longID) {
        LOG.info("lalelu" + longID);
        if (longID == 0) {
            return null;
        }
        TypedQuery<Household> query = em.createNamedQuery("Household.findById", Household.class)
                .setParameter("householdID", longID);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        LOG.info(query.getSingleResult().getName());
        return query.getSingleResult();
    }
    
    

    @Override
    public Place createPlace(String name, Household household) {
        em.setFlushMode(FlushModeType.AUTO);
        Place place = new Place(name, household);
        em.persist(place);
        place = em.merge(place);
        em.flush();
        return place;
    }

    @Override
    public boolean deletePlace(Place place) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            em.remove(place);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Place changePlace(Place place, String newName) {
        em.setFlushMode(FlushModeType.AUTO);
        if (newName != null) {
            place.setName(newName);
        }
        place = em.merge(place);
        em.flush();
        return place;
    }

    @Override
    public Place findPlace(long longID) {
        LOG.info("lalelu" + longID);
        if (longID == 0) {
            return null;
        }
        TypedQuery<Place> query = em.createNamedQuery("Place.findById", Place.class)
                .setParameter("placeID", longID);
        if (query.getResultList().isEmpty()) {
            return null;
        }
        LOG.info(query.getSingleResult().getName());
        return query.getSingleResult();
    }
    
    

    @Override
    public boolean addUserToHousehold(Household household, AppUser user) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            //???weitere Prüfung notwendig
            if (user != null) {
                household.getAppUserList().add(user);
            }
            household = em.merge(household);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeUserFromHousehold(Household household, AppUser user) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            if (user != null) {
                household.getAppUserList().remove(user);
            }
            household = em.merge(household);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addPlaceToHousehold(Household household, Place place) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            //???weitere Prüfung notwendig
            if (place != null) {
                household.getPlaceList().add(place);
            }
            household = em.merge(household);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removePlaceFromHousehold(Household household, Place place) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            if (place != null) {
                household.getAppUserList().remove(place);
            }
            household = em.merge(household);
            em.flush();
            deletePlace(place);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addStocksArticleToPlace(Place place, StocksArticle stocksArticle) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            //???weitere Prüfung notwendig
            if (stocksArticle != null) {
                place.getStocksArticleList().add(stocksArticle);
            }
            place = em.merge(place);
            em.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeStocksArticleFromPlace(Place place, StocksArticle stocksArticle) {
        try {
            em.setFlushMode(FlushModeType.AUTO);
            if (stocksArticle != null) {
                place.getStocksArticleList().remove(stocksArticle);
            }
            place = em.merge(place);
            em.flush();
            deletePlace(place);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
    @Override
    public List<Household> getHouseholdsForUser(AppUser user) {
        return em.createNamedQuery("Household.findByUser", Household.class).getResultList();
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    /*unnötig
     private Household getHousehold(long householdID) {
     return em.find(Household.class, householdID);
     //throw new UnsupportedOperationException("Not supported yet."); 
     //To change body of generated methods, choose Tools | Templates.
     }*/
}
