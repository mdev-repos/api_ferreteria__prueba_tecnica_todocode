package com.mdev.ferreteria.dto.request;

import com.mdev.ferreteria.model.enums.ToolCategory;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ToolUpdateRequestDTO(
        String name,
        String brand,
        ToolCategory category,
        @Positive Double price,
        @PositiveOrZero Integer stock,
        String description
) {}
