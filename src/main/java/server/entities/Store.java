package server.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Store {

    private String code; 

    private String address;

    private String city;

    private String province;

    private boolean active;

    private List<Stock> stocks = new ArrayList<>();

    private Set<User> users = new HashSet<>();

}
