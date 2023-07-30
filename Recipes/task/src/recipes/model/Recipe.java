package recipes.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @UpdateTimestamp
    private LocalDateTime date = LocalDateTime.now();

    @NotNull
    @NotBlank
    private String category;

    @NotNull
    @NotBlank
    private String description;

    @ElementCollection
    @Size(min = 1)
    private List<String> ingredients = new ArrayList<>();
    @ElementCollection
    @Size(min = 1)
    private List<String> directions = new ArrayList<>();


}

