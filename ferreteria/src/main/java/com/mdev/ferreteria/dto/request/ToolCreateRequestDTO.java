package com.mdev.ferreteria.dto.request;

import com.mdev.ferreteria.model.enums.ToolCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ToolCreateRequestDTO(
        @NotBlank String name,
        @NotBlank String brand,
        @NotNull ToolCategory category,
        @Positive double price,
        @PositiveOrZero int stock,
        String description
        ) {}