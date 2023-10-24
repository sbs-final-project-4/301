package com.yk.Motivation.domain.book.repository;

import com.yk.Motivation.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {
    Page<Book> findByBookTags_content(String tagContent, Pageable pageable);
}