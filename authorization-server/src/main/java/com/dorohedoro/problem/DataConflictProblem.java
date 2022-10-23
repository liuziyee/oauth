package com.dorohedoro.problem;

import com.dorohedoro.config.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DataConflictProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/data-conflict");

    public DataConflictProblem(String message) {
        super(TYPE, "元数据校验失败", Status.CONFLICT, message);
    }
}
