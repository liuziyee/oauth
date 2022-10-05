package com.dorohedoro.exception;

import com.dorohedoro.config.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvalidParamProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/invalid-param");

    public InvalidParamProblem(String message) {
        super(TYPE, "无效参数", Status.BAD_REQUEST, message);
    }
}
