package com.seabuhi.seacredit.module.analytics;

import com.seabuhi.seacredit.module.fraud.FraudAlertRepository;
import com.seabuhi.seacredit.module.notification.NotificationRepository;
import com.seabuhi.seacredit.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository         userRepository;
    private final FraudAlertRepository   fraudAlertRepository;
    private final NotificationRepository notificationRepository;

    @Cacheable(value = "dashboardStats", key = "'snapshot'")
    public Map<String, Object> getDashboardSnapshot() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",        userRepository.countByDeletedFalse());
        stats.put("activeUsers",       userRepository.countByActiveTrueAndDeletedFalse());
        stats.put("unverifiedUsers",   userRepository.countByVerifiedFalseAndDeletedFalse());
        stats.put("openFraudAlerts",   fraudAlertRepository.countByResolvedFalse());
        stats.put("totalFraudAlerts",  fraudAlertRepository.count());

        long total  = notificationRepository.count();
        long failed = notificationRepository.findByStatus("FAILED").size();
        stats.put("totalNotifications",  total);
        stats.put("sentNotifications",   total - failed);
        stats.put("failedNotifications", failed);
        return stats;
    }

    @Cacheable(value = "userStats", key = "'users'")
    public Map<String, Object> getUserStats() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total",      userRepository.countByDeletedFalse());
        s.put("active",     userRepository.countByActiveTrueAndDeletedFalse());
        s.put("unverified", userRepository.countByVerifiedFalseAndDeletedFalse());
        return s;
    }

    @Cacheable(value = "fraudStats", key = "'fraud'")
    public Map<String, Object> getFraudStats() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total",      fraudAlertRepository.count());
        s.put("openAlerts", fraudAlertRepository.countByResolvedFalse());
        s.put("critical",   fraudAlertRepository.findBySeverity("CRITICAL").size());
        s.put("high",       fraudAlertRepository.findBySeverity("HIGH").size());
        s.put("medium",     fraudAlertRepository.findBySeverity("MEDIUM").size());
        return s;
    }

    @Cacheable(value = "notificationStats", key = "'notif'")
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> s = new LinkedHashMap<>();
        long total   = notificationRepository.count();
        long failed  = notificationRepository.findByStatus("FAILED").size();
        long retried = notificationRepository.findByStatus("RETRIED").size();
        s.put("total",   total);
        s.put("sent",    total - failed - retried);
        s.put("failed",  failed);
        s.put("retried", retried);
        return s;
    }
}
