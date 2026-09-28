package com.example.AuthService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.AuthService.dto.NotificationRequest;

@FeignClient(name = "notification-service", url = "${NOTIFICATION_SERVICE_URL:http://localhost:8083}")
public interface NotificationClient {

    @PostMapping("/api/notification/send")
    void sendNotification(@RequestBody NotificationRequest request);
}