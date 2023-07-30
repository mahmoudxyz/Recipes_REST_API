package recipes.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.model.Recipe;
import recipes.model.UserAccount;
import recipes.repo.RecipeRepo;
import recipes.repo.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {


    private final RecipeRepo recipeRepo;

    private final UserRepository userRepository;

    @PostMapping("/new")
    @Valid
    public Map<String, Integer> addRecipe(@Valid @RequestBody Recipe recipe, HttpServletRequest request) {
        Map<String,Integer> backResponse = new HashMap<>();
        UserAccount userAccount = userRepository.findUserByEmail(extractAuthEmail(request));
        Recipe recipe1 =  recipeRepo.save(recipe);
        userAccount.getQuizzes().add(recipe1.getId());
        userRepository.save(userAccount);
        backResponse.put("id",recipe1.getId());
        return backResponse;
    }

    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable Integer id) {
        return recipeRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deleteRecipe(@PathVariable Integer id, HttpServletRequest request) {
        UserAccount userAccount = userRepository.findUserByEmail(extractAuthEmail(request));
        if (!userAccount.getQuizzes().contains(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (recipeRepo.existsById(id)) {
            recipeRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity updateRecipe(@PathVariable Integer id, @Valid @RequestBody Recipe newRecipe, HttpServletRequest request) {
        UserAccount userAccount = userRepository.findUserByEmail(extractAuthEmail(request));
        if (!userAccount.getQuizzes().contains(id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (recipeRepo.existsById(id)) {
            Recipe recipe =  recipeRepo.getById(id);
            recipe.setCategory(newRecipe.getCategory());
            recipe.setDescription(newRecipe.getDescription());
            recipe.setDirections(newRecipe.getDirections());
            recipe.setIngredients(newRecipe.getIngredients());
            recipe.setName(newRecipe.getName());
            recipeRepo.save(recipe);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam(required = false) String category,
                                                      @RequestParam(required = false) String name) {
        // Check if both or none of the parameters are provided
        if ((!StringUtils.hasText(category) && !StringUtils.hasText(name)) ||
                (StringUtils.hasText(category) && StringUtils.hasText(name))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<Recipe> recipes;

        if (StringUtils.hasText(category)) {
            // Search by category (case-insensitive)
            recipes = recipeRepo.findByCategoryIgnoreCaseOrderByDateDesc(category);
        } else {
            // Search by name (case-insensitive)
            recipes = recipeRepo.findByNameContainingIgnoreCaseOrderByDateDesc(name);
        }

        return ResponseEntity.ok(recipes);
    }

    private String extractAuthEmail(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String credentials = authorizationHeader.substring(6);
        byte[] decodedBytes = Base64.getDecoder().decode(credentials);
        String decodedCredentials = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] parts = decodedCredentials.split(":");
        return parts[0];
    }


}
