package eus.ibai.urlshortener.repository;

import eus.ibai.urlshortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {

    Optional<ShortUrl> findByKey(String key);

    @Modifying
    @Query("UPDATE ShortUrl su SET su.enabled = false WHERE su.id = :id AND su.enabled = true")
    int disableById(@Param("id") UUID id);

    @Query("FROM ShortUrl su WHERE su.enabled = true")
    List<ShortUrl> findAllEnabled();

    @Query("FROM ShortUrl su WHERE su.id = :id AND su.enabled = true")
    Optional<ShortUrl> findByIdIfEnabled(@Param("id") UUID id);
}
