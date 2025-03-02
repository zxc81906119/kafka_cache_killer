package com.redhat.kafkacachekiller.status;

public enum Status {
    SUCCESS,
    ASYNC,
    NOTIFY_SUCCESS,
    NOTIFY_FAILURE,
    TIMEOUT,
    FAILED,
    INTERRUPTED
}
