package spring.movieclinic.omdb;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import spring.movieclinic.category.Category;
import spring.movieclinic.category.CategoryRepository;
import spring.movieclinic.movie.Movie;
import spring.movieclinic.movie.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OmdbService {
    private final OmdbGateway omdbGateway;
    private final CategoryRepository categoryRepository;
    private final MovieRepository movieRepository;

    List<OmdbMovie> findMovies(String title) {
        List<OmdbMovie> found = omdbGateway.searchBy(title);
        List<OmdbMovie> movies = new ArrayList<>(found.size());
        for (OmdbMovie movie : found) {
            movies.add(omdbGateway.getBy(movie.getId()));
        }
        return movies;
    }

    List<Movie> saveMovie(String id, OmdbMoviesList list) {
        List<OmdbMovie> omdbMovies = list.getOmdbMovies();
        for (OmdbMovie omdbMovie : omdbMovies) {
            if (id.equals(omdbMovie.getId())) {
                Movie movie = new Movie();
                movie.setName(omdbMovie.getTitle());
                movie.setDescription(omdbMovie.getPlot());
                movie.setYear(omdbMovie.getYear());
                movie.setPictureURL(omdbMovie.getPoster());

                String categories = omdbMovie.getGenre();
                String[] genres = categories.split(", ");
                for (String genre : genres) {
                    Optional<Category> optional = categoryRepository.findByName(genre);
                    optional.ifPresent(movie::addCategory);
                }
                movieRepository.save(movie);
                break;
            }
        }
        return movieRepository.findByOrderByNameAsc();
    }
}
