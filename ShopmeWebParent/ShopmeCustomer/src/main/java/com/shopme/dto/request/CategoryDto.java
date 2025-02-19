package com.shopme.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Builder
@Data
public class CategoryDto {
    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 3, max = 100, message = "Category name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Alias cannot be empty")
    @Size(min = 3, max = 50, message = "Alias must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Alias can only contain lowercase letters, numbers, and hyphens")
    private String alias;

    @NotBlank(message = "Image URL cannot be empty")
    private String image;

    private boolean enabled;

    @Positive(message = "Parent ID must be greater than 0")
    private Long parentId;
}
