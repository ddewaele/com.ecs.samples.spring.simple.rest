package com.ecs.samples.spring.simple.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ecs.samples.spring.simple.rest.model.User;

/**
 * Simple repository interface for {@link User} instances. The interface is used to declare so called query methods,
 * methods to retrieve single entities or collections of them.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public interface UserRepository extends CrudRepository<User, Integer> {

	/**
	 * Find the user with the given username. This method will be translated into a query using the
	 * {@link javax.persistence.NamedQuery} annotation at the {@link User} class.
	 * 
	 * @param lastname
	 * @return
	 */
	User findByTheEmail(String email);

	/**
	 * Find all users with the given lastname. This method will be translated into a query by constructing it directly
	 * from the method name as there is no other query declared.
	 * 
	 * @param lastname
	 * @return
	 */
	//List<User> findByLastname(String lastname);

	/**
	 * Returns all users with the given firstname. This method will be translated into a query using the one declared in
	 * the {@link Query} annotation declared one.
	 * 
	 * @param firstname
	 * @return
	 */
	@Query("select u from User u where u.email= ?")
	List<User> findByEmail(String email);

	/**
	 * Returns all users with the given name as first- or lastname. Makes use of the {@link Param} annotation to use named
	 * parameters in queries. This makes the query to method relation much more refactoring safe as the order of the
	 * method parameters is completely irrelevant.
	 * 
	 * @param name
	 * @return
	 */
	@Query("select u from User u where u.email= :name or u.id= :name")
	List<User> findByEmailOrId(@Param("name") String name);
}