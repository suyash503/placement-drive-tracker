package com.placement.entity

enum class DriveStatus {
    OPEN,      // students can apply
    CLOSED,    // applications stopped, interviews going on
    COMPLETED  // results announced
}

enum class ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    SELECTED,
    REJECTED
}

enum class SlotResult {
    PENDING,
    PASSED,
    FAILED
}
