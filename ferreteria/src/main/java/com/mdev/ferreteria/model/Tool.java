package com.mdev.ferreteria.model;

import com.mdev.ferreteria.model.enums.ToolCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private String brand;

    @Enumerated(EnumType.STRING)
    private ToolCategory category;

    private double price;
    private int stock;
    private String description;
}
