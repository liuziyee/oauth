package com.dorohedoro.exception;

import com.dorohedoro.config.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DuplicateProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/duplicate");

    public DuplicateProblem(String message) {
        super(TYPE, "重复数据", Status.CONFLICT, message);
    }
}
