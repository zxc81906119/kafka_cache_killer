package com.redhat.kafkacachekiller.status;

public enum Status {
    SUCCESS,
    ASYNC_NOTIFY,
    SYNC_NOTIFY_SUCCESS,
    SYNC_NOTIFY_FAILURE,
    SYNC_NOTIFY_TIMEOUT,
    FAILED
}
