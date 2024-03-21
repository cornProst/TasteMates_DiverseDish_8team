package TasteMates.DiverseDish.recipe;

import TasteMates.DiverseDish.recipe.entity.Recipe;
import TasteMates.DiverseDish.recipe.repo.RecipeRepo;
import TasteMates.DiverseDish.recipe.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepo recipeRepo;

    // 레시피 생성
    public RecipeDto create(RecipeDto dto, MultipartFile main_image) throws IOException {
        Files.createDirectories(Path.of("media"));
        UUID uuid = UUID.randomUUID();
        Path path = Path.of("media/" + main_image.getOriginalFilename() + "_" + uuid); // 해당 파일의 이름을 경로를 포함해서 지정
        main_image.transferTo(path); // 위에서 지정한 경로로 해당 파일 저장

        return RecipeDto.fromEntity(recipeRepo.save(Recipe.builder()
                .user(null)
                .main_image(path.toString())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .video_link(dto.getVideo_link())
                .view(0) // 초기 조회수 0
                .level(dto.getLevel())
                .category(dto.getCategory())
                .ingredient(dto.getIngredient())
                .approval(0) // 승인을 해줘야 게시 가능, 초기엔 미승인 상태
                .build()));
    }

    // 레시피 조회
    public RecipeDto readOne(Long recipeId) {
        Recipe recipe = getRecipe(recipeId);
        return RecipeDto.fromEntity(recipe);
    }

    // 레시피 업데이트
    public RecipeDto updateRecipe(Long recipeId, RecipeDto dto) {
        Recipe recipe = getRecipe(recipeId);
        recipe.setMain_image(dto.getMain_image());
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setVideo_link(dto.getVideo_link());
        recipe.setLevel(dto.getLevel());
        recipe.setCategory(dto.getCategory());
        recipe.setIngredient(dto.getIngredient());
        // TODO: 업데이트한 경우 재승인할지

        return RecipeDto.fromEntity(recipeRepo.save(recipe));
    }

    // 레시피 삭제
    public void delete(Long recipeId) {
        recipeRepo.delete(getRecipe(recipeId));
    }



    // ID로 레시피 조회하기
    public Recipe getRecipe(Long id) {
        Optional<Recipe> optionalRecipe = recipeRepo.findById(id);
        if (optionalRecipe.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalRecipe.get();
    }
}
