package com.va1err.IssueTracker.repositories;

import com.va1err.IssueTracker.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByOwnerIdAndProjectId(Long ownerId, Long projectId);
    Optional<Issue> findByOwnerIdAndProjectIdAndId(Long ownerId, Long projectId, Long id);
}
