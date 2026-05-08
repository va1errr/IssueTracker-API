package com.va1err.IssueTracker.repositories;

import com.va1err.IssueTracker.enums.Priority;
import com.va1err.IssueTracker.enums.Status;
import com.va1err.IssueTracker.models.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    Page<Issue> findAllByOwnerIdAndProjectId(Pageable pageable, Long ownerId, Long projectId);
    Page<Issue> findAllByStatus(Pageable pageable, Status status);
    Page<Issue> findAllByPriority(Pageable pageable, Priority priority);
    Page<Issue> findAllByStatusAndPriority(Pageable pageable, Status status, Priority priority);
    Page<Issue> findAllByOwnerIdAndProjectIdAndStatus(Pageable pageable, Status status, Long ownerId, Long projectId);
    Page<Issue> findAllByOwnerIdAndProjectIdAndPriority(Pageable pageable, Priority priority, Long ownerId, Long projectId);
    Page<Issue> findAllByOwnerIdAndProjectIdAndStatusAndPriority(Pageable pageable, Status status, Priority priority, Long ownerId, Long projectId);
    Optional<Issue> findByOwnerIdAndProjectIdAndId(Long ownerId, Long projectId, Long id);
}
