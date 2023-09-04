package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user = userRepository.save(user);
       return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        User user = userRepository.findById(userId).get();
        int countWebSeries = 0;
        for (WebSeries webseries :
                webSeriesList) {
            // sort on the bases of agelimit
            if ( user.getAge() <= webseries.getAgeLimit()){
                SubscriptionType userSubscription = user.getSubscription().getSubscriptionType();
                SubscriptionType webseriesSubscription = webseries.getSubscriptionType();
                if (userSubscription.equals(webseriesSubscription)){
                    countWebSeries++;
                } else if (userSubscription == SubscriptionType.ELITE) {
                    countWebSeries++;
                } else if (userSubscription == SubscriptionType.PRO && webseriesSubscription == SubscriptionType.BASIC ) {
                    countWebSeries++;
                }
            }
        }
        return countWebSeries;
    }


}
