package com.shopme;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MvcConfig implements WebMvcConfigurer {

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String listDirName = "uploads/categories-images, uploads/brandLogo, uploads/product-images";
        List<String> dirNames = Arrays.asList(listDirName.split(", "));

        for(String dirName : dirNames) {
            Path userPhotosDir = Paths.get(dirName);
            String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
            registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotosPath + "/");
        }
    }
}
