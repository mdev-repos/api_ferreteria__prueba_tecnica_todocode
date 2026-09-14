package com.mdev.ferreteria.controller;

import com.mdev.ferreteria.dto.request.ToolCreateRequestDTO;
import com.mdev.ferreteria.dto.request.ToolUpdateRequestDTO;
import com.mdev.ferreteria.dto.response.ToolResponseDTO;
import com.mdev.ferreteria.service.IToolService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tools")
public class ToolController {
    private final IToolService toolServ;

    public ToolController(IToolService toolService){
        this.toolServ = toolService;
    }

    @PostMapping("/create")
    public ResponseEntity<ToolResponseDTO> createTool(@Valid @RequestBody ToolCreateRequestDTO request){
        ToolResponseDTO created = toolServ.createTool(request);
        URI location = URI.create("/tools/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToolResponseDTO> getToolById(@PathVariable Long id) {
        ToolResponseDTO tool = toolServ.getToolById(id);
        if (tool == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tool);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ToolResponseDTO>> getAllTools() {
        List<ToolResponseDTO> tools = toolServ.getAllTools();
        return ResponseEntity.ok(tools);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ToolResponseDTO> updateTool(@PathVariable Long id,
                                                      @Valid @RequestBody ToolUpdateRequestDTO request) {
        ToolResponseDTO updated = toolServ.updateTool(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTool(@PathVariable Long id) {
        toolServ.deleteToolById(id);
        return ResponseEntity.noContent().build();
    }
}
