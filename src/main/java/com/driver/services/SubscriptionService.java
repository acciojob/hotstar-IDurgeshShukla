package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        User user = userRepository.findById(subscriptionEntryDto.getUserId())
                .get();

        //Save The subscription Object into the Db and return the total Amount that user has to pay
       int totalAmountPaid = 0;
       SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();

       if (subscriptionType.equals(SubscriptionType.BASIC)){
           totalAmountPaid = 500 + 200*(subscriptionEntryDto.getNoOfScreensRequired());
       } else if (subscriptionType.equals(SubscriptionType.PRO)) {
           totalAmountPaid = 800 + 250*(subscriptionEntryDto.getNoOfScreensRequired());
       } else {
           totalAmountPaid = 1000 + 300*(subscriptionEntryDto.getNoOfScreensRequired());
       }

        Subscription subscription = new Subscription(subscriptionEntryDto.getSubscriptionType(),
               subscriptionEntryDto.getNoOfScreensRequired(),new Date(),totalAmountPaid);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        user.setSubscription(subscription);
        return totalAmountPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{
        User user = userRepository.findById(userId).get();
        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        if (user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)) throw new Exception("Already" +
                " the best Subscription");
        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        int oldpayment = 0;
        int userHasToPay = 0;
        if (subscriptionType.equals(SubscriptionType.BASIC)){
            int payment =  800 + 250*subscription.getNoOfScreensSubscribed();
            oldpayment = subscription.getTotalAmountPaid();
            userHasToPay = payment - oldpayment;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(userHasToPay);
            user.setSubscription(subscription);
        } else if (subscriptionType.equals(SubscriptionType.PRO)){
            int payment =  1000 + 300*subscription.getNoOfScreensSubscribed();
            oldpayment = subscription.getTotalAmountPaid();
            userHasToPay = payment - oldpayment;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(userHasToPay);
            user.setSubscription(subscription);
        }
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        userRepository.save(user);
        return userHasToPay;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int revenue = 0;
        for (Subscription subscription :
                subscriptionList) {
            revenue += subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
