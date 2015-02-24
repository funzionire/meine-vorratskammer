/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author baader
 */
public class Household {
    private static int id = 0;
    private String name;
    

    public Household(String name) {
        this.name = name;
        id = id++;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Household.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Place addPlace(String name){
        return new Place(name, id);
    }    
    
}
