package com.example.restapi.common;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.restapi.generated.model.BadRequestError;
import com.example.restapi.generated.model.InvalidParam;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;

public class BadRequestCreator {

    public static BadRequestError from(MethodArgumentNotValidException ex) {
        var invalidList = ex.getFieldErrors()
            .stream()
            .map(BadRequestCreator::createInvalidParam)
            .collect(Collectors.toList());
        var error = new BadRequestError();
        error.setInvalidParams(invalidList);
        return error;
    }

    private static InvalidParam createInvalidParam(FieldError fieldError) {
        var invalidParam = new InvalidParam();
        invalidParam.setName(fieldError.getField());
        invalidParam.setReason(fieldError.getDefaultMessage());
        return invalidParam;
    }

    public static BadRequestError from(ConstraintViolationException ex) {
        var invalidParamList = ex.getConstraintViolations()
            .stream()
            .map(violation -> {
                var parameterOpt = StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                    .filter(node -> node.getKind().equals(ElementKind.PARAMETER))
                    .findFirst();
                var invalidParam = new InvalidParam();
                parameterOpt.ifPresent(p -> invalidParam.setName(p.getName()));
                invalidParam.setReason(violation.getMessage());
                return invalidParam;
            }).collect(Collectors.toList());

        var error = new BadRequestError();
        error.setInvalidParams(invalidParamList);
        return error;
    }

}
