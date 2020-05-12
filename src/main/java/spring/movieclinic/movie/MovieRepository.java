package spring.movieclinic.movie;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import spring.movieclinic.category.Category;

import java.util.List;
import java.util.Set;

public interface MovieRepository extends CrudRepository<Movie, Integer> {

    List<Movie> findByNameContains(String name);


    @Query(value = "SELECT DISTINCT movie.* FROM Movies movie " +
            "INNER JOIN movie_category m_c " +
            "ON (movie.id = m_c.movie_id AND (:categories is null or m_c.category_id IN :categories))" +
            "WHERE movie.name LIKE %:name% and " +
            "(:year is null or movie.year = :year) and " +
            "movie.description LIKE %:description%", nativeQuery = true)
    List<Movie> findByNameAndYearAndCategoriesAndDescription(@Param("categories") Set<Integer> categories,
                                                             @Param("name") String name,
                                                             @Param("year") Integer year,
                                                             @Param("description") String description);

    List<Movie> findByOrderByNameAsc();

}
