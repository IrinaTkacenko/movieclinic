package spring.movieclinic.omdb;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@AllArgsConstructor
public class OmdbController {
    private final OmdbService omdbSearchService;

    @GetMapping("omdb/find")
    public String searchForm(Model model) {
        model.addAttribute("omdbMovie", new OmdbMovie());
        return "omdb/omdb-form";
    }

    // /omdb/search?name=
    @GetMapping("omdb/search")
    public String searchForMovies(@RequestParam String title, Model model) {
        model.addAttribute("omdbMovie", new OmdbMovie());
        model.addAttribute("omdbMovies", omdbSearchService.findMovies(title));
        return "omdb/omdb-search";
    }

    @PostMapping("omdb/save/{id}")
    public String saveMovie(@PathVariable("id") String id, @ModelAttribute OmdbMoviesList omdbMovies, Model model) {
        model.addAttribute("movies", omdbSearchService.saveMovie(id, omdbMovies));
        return "movies/movies-list";
    }
}


