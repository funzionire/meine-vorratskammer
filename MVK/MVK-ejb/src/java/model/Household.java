/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import javax.persistence.*;

/**
 *
 * @author baader
 */
@Entity
public class Household {
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long householdID;
    @OneToMany
    private List<Place> places;
    @ManyToMany
    private List<User> users;

    public Household(String name, User user) {
        users.add(user);
        this.name = name;
    }

    public long getHouseholdID() {
        return householdID;
    }

    public void setHouseholdID(long householdID) {
        this.householdID = householdID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean addUser(User user){
        return users.add(user);
    }
    
    public boolean removeUser(User user){
        return users.remove(user);
    }
    
     public boolean createPlace(String name, Household household){
     return places.add(new Place(name, household));
     }    
     
     public boolean removePlace(String name){
         //gemockt
         //müsste eigentlich removeArticles aufrufen und removeArticles müsste removeUnits machen
         return true;
     }
}
