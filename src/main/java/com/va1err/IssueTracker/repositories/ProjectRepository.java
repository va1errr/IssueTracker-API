package com.va1err.IssueTracker.repositories;

import com.va1err.IssueTracker.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOwnerId(Long ownerId);
    Optional<Project> findByOwnerIdAndId(Long ownerId, Long id);
    boolean existsByOwnerIdAndId(Long ownerId, Long id);
}
