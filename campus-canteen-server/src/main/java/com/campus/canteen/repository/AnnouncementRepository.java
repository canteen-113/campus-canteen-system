package com.campus.canteen.repository;

import com.campus.canteen.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByStatusOrderByCreateTimeDesc(Announcement.Status status);
}
