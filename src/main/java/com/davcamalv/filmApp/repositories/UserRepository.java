package com.davcamalv.filmApp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.davcamalv.filmApp.domain.MediaContent;
import com.davcamalv.filmApp.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);
	
	@Query("select u.toWatchList from User u where u.username = ?1")
	List<MediaContent> getToWatchListByUsername(Pageable pageable, String username);

	@Query("select count(*) > 0 from User u inner join u.toWatchList t where u.id = ?1 and t.id = ?2")
	boolean existsOnToWatchList(Long idUser, Long id);
}
