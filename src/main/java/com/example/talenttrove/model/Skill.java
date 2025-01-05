package com.example.talenttrove.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {

    @JsonProperty("name")
    private String name;

    @JsonProperty("level")
    private String level;
}
