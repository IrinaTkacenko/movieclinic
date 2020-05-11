package spring.movieclinic.movie;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import spring.movieclinic.category.Category;

import java.util.List;
import java.util.Set;

public interface MovieRepository extends CrudRepository<Movie, Integer> {

    List<Movie> findByNameContains(String name);

    @Query(value = "SELECT * FROM movies WHERE name LIKE %:name% and " +
            "(:year is null or year = :year) and " +
            "(:categories is null or id IN (SELECT movie_id FROM movie_category WHERE category_id IN " +
            "(SELECT id FROM categories WHERE name IN :categories))) and " +
            "description LIKE %:description%", nativeQuery = true)
    List<Movie> findByNameAndYearAndCategoriesAndDescription(@Param("name") String name,
                                                             @Param("year") Integer year,
                                                             @Param("categories") Set<String> categories,
                                                             @Param("description") String description);

    List<Movie> findByOrderByNameAsc();

}
