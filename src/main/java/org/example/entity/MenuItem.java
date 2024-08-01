package org.example.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItem {
    private Long id;

    private Long parent;

    private String key;

    private String value;

    private String target;

    private String service;

    private String gridDef;

    private String tooltip;

    private String image;

    private Boolean expanded;

    @Override
    public String toString() {
        return this.value;
    }

}
