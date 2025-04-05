package com.codingShuttle.SecurityApp.SecurityApplication.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController{

    @GetMapping("/free")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> freeSubscription() {
        return ResponseEntity.ok("Free Subscription");
    }

    @GetMapping("/basic")
    @PreAuthorize("@subscriptionService.isAtleastBasic()")
    public ResponseEntity<String> basicSubscription() {
        return ResponseEntity.ok("Accessed BASIC feature! (Requires BASIC or PREMIUM plan)");
    }

    @GetMapping("/premium")
    @PreAuthorize("@subscriptionService.isPremium()")
    public ResponseEntity<String> premiumSubscription() {
        return ResponseEntity.ok("Accessed PREMIUM feature! (Requires PREMIUM plan)");
    }

    @GetMapping("/admin-premium")
    @PreAuthorize("hasRole('ADMIN') and @subscriptionService.isPremium()")
    public ResponseEntity<String> adminPremiumSubscription(){
        return ResponseEntity.ok("Accessed ADMIN PREMIUM feature! (Requires ADMIN role and a PREMIUM plan)");
    }

}