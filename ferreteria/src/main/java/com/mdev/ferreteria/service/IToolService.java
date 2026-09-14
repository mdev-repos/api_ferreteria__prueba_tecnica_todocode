package com.mdev.ferreteria.service;

import com.mdev.ferreteria.dto.request.ToolCreateRequestDTO;
import com.mdev.ferreteria.dto.request.ToolUpdateRequestDTO;
import com.mdev.ferreteria.dto.response.ToolResponseDTO;

import java.util.List;

public interface IToolService {
    public ToolResponseDTO createTool(ToolCreateRequestDTO request);
    public ToolResponseDTO getToolById(Long id);
    public List<ToolResponseDTO> getAllTools();
    public ToolResponseDTO updateTool(Long id, ToolUpdateRequestDTO request);
    public void deleteToolById(Long id);
}
