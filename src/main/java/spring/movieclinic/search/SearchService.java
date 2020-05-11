package spring.movieclinic.search;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import spring.movieclinic.movie.Movie;
import spring.movieclinic.movie.MovieRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class SearchService {

    private final MovieRepository movieRepository;
    private final String[] stopWords = new String[]{"a", "an", "and", "as", "at", "be", "by", "if", "in","is",
            "no", "of", "on", "or", "the", "to"};

    List<Movie> getSearchBarResults(String query) {
        return checkQueryIfNotValid(query) ? Collections.emptyList() :
                movieRepository.findByNameContains(query.trim());
    }

    List<Movie> getAdvancedSearchResults(String query, Movie movie) {
        return query == null ? advancedSearch(movie) : getSearchBarResults(query);
    }

    String getQueryToDisplay(String query, Movie movie) {
        return query != null ? query : advancedSearchQuery(movie);
    }

    private List<Movie> advancedSearch(Movie movie) {

        Set<String> set = new LinkedHashSet<>();

        movie.getCategories().forEach(category -> set.add(category.getName()));

        return checkQueryIfNotValid(movie.getName()) &&
                movie.getYear() == null &&
                movie.getCategories().isEmpty() &&
                checkQueryIfNotValid(movie.getDescription()) ?
                Collections.emptyList() :
                new ArrayList<>(movieRepository.findByNameAndYearAndCategoriesAndDescription(
                        movie.getName(),
                        movie.getYear(),
                        set,
                        movie.getDescription()));
    }

    private Boolean checkQueryIfNotValid(String query) {
        return query.trim().isEmpty() || checkForStopWords(query.trim()).equals("");
    }

    private String checkForStopWords(String trimmedQuery) {
        for(String word:stopWords) {
            if(trimmedQuery.equals(word)) {
                return "";
            }
        }
        return trimmedQuery;
    }

    private String advancedSearchQuery(Movie movie) {
        return (!movie.getName().isEmpty() ? "Title: " + movie.getName() + " " : "") +
                (movie.getYear() != null ? "Year: " + movie.getYear() + " " : "") +
                (!movie.getCategories().isEmpty()? "Category: " + movie.getCategories() + " " : "") +
                (!movie.getDescription().isEmpty()? "Description: " + movie.getDescription() : "");
    }
}