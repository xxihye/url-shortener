package com.urlshortener.url.repository;

import com.urlshortener.url.domain.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortKey(String shortKey);
}
