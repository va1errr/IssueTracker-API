package com.va1err.IssueTracker.exceptions;

public class IssueNotFoundException extends RuntimeException {
    public IssueNotFoundException(Long id) {
        super("Issue with id = " + id + " not found");
    }
}
