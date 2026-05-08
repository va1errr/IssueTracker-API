package com.va1err.IssueTracker.repositories;

import com.va1err.IssueTracker.enums.Role;
import com.va1err.IssueTracker.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByRole(Role role, Pageable pageable);

}
