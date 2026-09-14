package com.mdev.ferreteria.mapper;

import com.mdev.ferreteria.dto.request.ToolCreateRequestDTO;
import com.mdev.ferreteria.dto.request.ToolUpdateRequestDTO;
import com.mdev.ferreteria.dto.response.ToolResponseDTO;
import com.mdev.ferreteria.model.Tool;

import java.util.ArrayList;
import java.util.List;

public class ToolMapper {

    public static Tool toEntity(ToolCreateRequestDTO dto){
        Tool tool = new Tool();
        tool.setName(dto.name());
        tool.setBrand(dto.brand());
        tool.setCategory(dto.category());
        tool.setPrice(dto.price());
        tool.setStock(dto.stock());
        tool.setDescription(dto.description());
        return tool;
    }

    public static ToolResponseDTO toResponseDTO(Tool tool){
        return new ToolResponseDTO(
                tool.getId(),
                tool.getName(),
                tool.getBrand(),
                tool.getCategory(),
                tool.getPrice(),
                tool.getStock(),
                tool.getDescription()
        );
    }

    public static List<ToolResponseDTO> toResponseDTOList (List<Tool> tools){
        List<ToolResponseDTO> result = new ArrayList<>();
        for(Tool tool : tools){
            result.add(toResponseDTO(tool));
        }
        return result;
    }

    public static void applyUpdate(Tool tool, ToolUpdateRequestDTO dto) {
        if (dto.name() != null) tool.setName(dto.name());
        if (dto.brand() != null) tool.setBrand(dto.brand());
        if (dto.category() != null) tool.setCategory(dto.category());
        if (dto.price() != null) tool.setPrice(dto.price());
        if (dto.stock() != null) tool.setStock(dto.stock());
        if (dto.description() != null) tool.setDescription(dto.description());
    }
}
