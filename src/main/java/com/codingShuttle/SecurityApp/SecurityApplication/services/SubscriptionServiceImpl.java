package com.codingShuttle.SecurityApp.SecurityApplication.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codingShuttle.SecurityApp.SecurityApplication.entities.User;
import com.codingShuttle.SecurityApp.SecurityApplication.entities.enums.SubscriptionPlans;

@Service("subscriptionService") // Give it a bean name for easy reference in @PreAuthorize
public class SubscriptionServiceImpl implements SubscriptionService {
    
    private User getCurrentUserSafely() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    @Override
    public boolean isPremium() {
        // TODO Auto-generated method stub
        User user = getCurrentUserSafely();
        return user.getSubscriptionPlan().equals(SubscriptionPlans.PREMIUM); 
    }

    @Override
    public boolean isAtleastBasic() {
        // TODO Auto-generated method stub
        User user = getCurrentUserSafely();
        return user.getSubscriptionPlan().equals(SubscriptionPlans.BASIC ) || user.getSubscriptionPlan().equals(SubscriptionPlans.PREMIUM);
        
    }
    
}
