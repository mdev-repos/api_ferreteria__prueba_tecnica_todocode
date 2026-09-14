package com.mdev.ferreteria.service.impl;

import com.mdev.ferreteria.dto.request.ToolCreateRequestDTO;
import com.mdev.ferreteria.dto.request.ToolUpdateRequestDTO;
import com.mdev.ferreteria.dto.response.ToolResponseDTO;
import com.mdev.ferreteria.mapper.ToolMapper;
import com.mdev.ferreteria.model.Tool;
import com.mdev.ferreteria.repository.IToolRepository;
import com.mdev.ferreteria.service.IToolService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolService implements IToolService {
    private final IToolRepository toolRepo;

    public ToolService(IToolRepository toolRepository){
        this.toolRepo = toolRepository;
    }

    @Override
    public ToolResponseDTO createTool(ToolCreateRequestDTO request) {
        Tool tool = ToolMapper.toEntity(request);
        Tool saved = toolRepo.save(tool);
        return ToolMapper.toResponseDTO(saved);
    }

    @Override
    public ToolResponseDTO getToolById(Long id) {
        Tool tool = toolRepo.findById(id).orElse(null);
        if(tool == null){
            return null;
        }
        return ToolMapper.toResponseDTO(tool);
    }

    @Override
    public List<ToolResponseDTO> getAllTools() {
        List<Tool> tools = toolRepo.findAll();
        return ToolMapper.toResponseDTOList(tools);
    }

    @Override
    public ToolResponseDTO updateTool(Long id, ToolUpdateRequestDTO request) {
        Tool tool = toolRepo.findById(id).orElse(null);
        if (tool == null) {
            return null;
        }
        ToolMapper.applyUpdate(tool, request);
        Tool saved = toolRepo.save(tool);
        return ToolMapper.toResponseDTO(saved);
    }

    @Override
    public void deleteToolById(Long id) {
        toolRepo.deleteById(id);
    }
}
