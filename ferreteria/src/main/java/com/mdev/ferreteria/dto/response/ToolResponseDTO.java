package com.mdev.ferreteria.dto.response;

import com.mdev.ferreteria.model.enums.ToolCategory;

public record ToolResponseDTO(
        Long id,
        String name,
        String brand,
        ToolCategory category,
        double price,
        int stock,
        String description
) {}