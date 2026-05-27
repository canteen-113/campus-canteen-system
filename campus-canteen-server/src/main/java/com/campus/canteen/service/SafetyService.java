package com.campus.canteen.service;

import com.campus.canteen.entity.HealthCheck;
import com.campus.canteen.entity.SafetySample;
import com.campus.canteen.repository.HealthCheckRepository;
import com.campus.canteen.repository.SafetySampleRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SafetyService {

    private final SafetySampleRepository sampleRepo;
    private final HealthCheckRepository healthCheckRepo;

    public SafetyService(SafetySampleRepository sampleRepo, HealthCheckRepository healthCheckRepo) {
        this.sampleRepo = sampleRepo;
        this.healthCheckRepo = healthCheckRepo;
    }

    public List<SafetySample> getSamples(Long canteenId) {
        if (canteenId != null) {
            return sampleRepo.findByCanteenId(canteenId);
        }
        return sampleRepo.findAll();
    }

    public SafetySample createSample(SafetySample sample) {
        if (sample.getStatus() == null) {
            sample.setStatus(SafetySample.Status.PENDING);
        }
        sample.setCreateTime(LocalDateTime.now());
        return sampleRepo.save(sample);
    }

    public void recordSample(Long id, String inspector, String image) {
        SafetySample sample = sampleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("留样记录不存在"));
        sample.setInspector(inspector);
        if (image != null) sample.setImage(image);
        sample.setStatus(SafetySample.Status.SAMPLED);
        sampleRepo.save(sample);
    }

    public void deleteSample(Long id) {
        sampleRepo.deleteById(id);
    }

    // ===== 员工晨检 =====
    public List<HealthCheck> getHealthChecks(LocalDate date) {
        if (date != null) return healthCheckRepo.findByCheckDate(date);
        return healthCheckRepo.findAll();
    }

    public HealthCheck createHealthCheck(HealthCheck check) {
        if (check.getCheckDate() == null) check.setCheckDate(LocalDate.now());
        if (check.getIsQualified() == null) check.setIsQualified(1);
        return healthCheckRepo.save(check);
    }

    public HealthCheck updateHealthCheck(Long id, HealthCheck update) {
        HealthCheck existing = healthCheckRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("晨检记录不存在"));
        if (update.getTemperature() != null) existing.setTemperature(update.getTemperature());
        if (update.getIsQualified() != null) existing.setIsQualified(update.getIsQualified());
        if (update.getRemark() != null) existing.setRemark(update.getRemark());
        return healthCheckRepo.save(existing);
    }
}
