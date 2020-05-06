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

        List<Movie> listOfFound = new ArrayList<>();

        listOfFound.addAll(checkQueryIfNotValid(movie.getName()) ?
                Collections.emptyList() : movieRepository.findByNameContains(movie.getName().trim()));
        listOfFound.addAll(movie.getYear() == null ?
                Collections.emptyList() : movieRepository.findByYear(movie.getYear()));
        listOfFound.addAll(movie.getCategories() == null ?
                Collections.emptyList() :movieRepository.findByCategoriesIn(movie.getCategories()));
        listOfFound.addAll(checkQueryIfNotValid(movie.getDescription()) ?
                Collections.emptyList() : movieRepository.findByDescriptionContains(movie.getDescription().trim()));

        Map<Movie, Integer> result = mapOfFound(listOfFound).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return new ArrayList<>(result.keySet());
    }

    private Map<Movie, Integer> mapOfFound(List<Movie> listOfFound) {

        Map<Movie, Integer> mapOfFound = new LinkedHashMap<>();
        if(!listOfFound.isEmpty()) {
            for (Movie mov:listOfFound) {
                int numberOfFieldsCorrespondsTo = 0;
                mapOfFound.put(mov, !mapOfFound.containsKey(mov) ? ++numberOfFieldsCorrespondsTo : mapOfFound.get(mov) + 1);
            }
        }
        return mapOfFound;
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