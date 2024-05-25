package com.shopme.admin.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not find any brand with ID")
public class BrandNotFoundException extends Throwable {
    public BrandNotFoundException(String s) {
    }
}
